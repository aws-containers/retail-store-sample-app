<#
.SYNOPSIS
    Automated Day 05 pre-Pull Request review (v5) for the AWS Retail Store DevOps Platform.

.DESCRIPTION
    Runs Git, Docker, source-build, image-build, runtime, security, health,
    functional, size, and proof checks for the Day 05 container-hardening task.

    The script does NOT push code or create a Pull Request.
    Staging and committing are optional switches and only run if all checks pass.

.REQUIREMENTS
    - Windows PowerShell 5.1+ or PowerShell 7+
    - Git
    - Docker Desktop running Linux containers
    - Docker Compose v2
    - Run from, or point -RepoPath to, the repository root

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\Invoke-Day05PrePRReview.ps1 `
      -RepoPath "C:\scripts\projects\retail-store-sample-app"

.EXAMPLE
    # Optional: only when the Catalog service exposes a real business endpoint.
    .\Invoke-Day05PrePRReview-v5.ps1 `
      -RepoPath "C:\scripts\projects\retail-store-sample-app" `
      -ComposeFile ".\src\app\docker-compose.yml" `
      -CatalogFunctionalPath "/your-existing-endpoint"

.EXAMPLE
    # Only after the report passes:
    .\Invoke-Day05PrePRReview.ps1 `
      -RepoPath "C:\scripts\projects\retail-store-sample-app" `
      -StageChanges `
      -CommitChanges
#>

[CmdletBinding()]
param(
    [Parameter()]
    [string]$RepoPath = (Get-Location).Path,

    [Parameter()]
    [string]$ComposeFile = "",

    [Parameter()]
    [string]$BaseBranch = "develop",

    [Parameter()]
    [string]$CheckoutHealthPath = "/health",

    [Parameter()]
    [string]$CatalogHealthPath = "/health",

    [Parameter()]
    [string]$CatalogFunctionalPath = "",

    [Parameter()]
    [int]$InternalPort = 8080,

    [Parameter()]
    [int]$HealthTimeoutSeconds = 240,

    [Parameter()]
    [switch]$SkipFetch,

    [Parameter()]
    [switch]$SkipJavaVerify,

    [Parameter()]
    [switch]$SkipRuntime,

    [Parameter()]
    [switch]$RunTrivy,

    [Parameter()]
    [switch]$StageChanges,

    [Parameter()]
    [switch]$CommitChanges,

    [Parameter()]
    [string]$CommitMessage = "chore(containers): complete day 05 image hardening"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$script:Failures = New-Object System.Collections.Generic.List[string]
$script:Warnings = New-Object System.Collections.Generic.List[string]
$script:Checks = New-Object System.Collections.Generic.List[object]
$script:CurrentCommand = ""

function Write-Section {
    param([string]$Title)
    Write-Host ""
    Write-Host ("=" * 88)
    Write-Host $Title
    Write-Host ("=" * 88)
}

function Add-Check {
    param(
        [string]$Name,
        [ValidateSet("PASS", "FAIL", "WARN", "SKIP")]
        [string]$Status,
        [string]$Details = ""
    )

    $script:Checks.Add([PSCustomObject]@{
        Time    = (Get-Date).ToString("s")
        Check   = $Name
        Status  = $Status
        Details = $Details
    })

    switch ($Status) {
        "PASS" { Write-Host "[PASS] $Name - $Details" -ForegroundColor Green }
        "FAIL" {
            Write-Host "[FAIL] $Name - $Details" -ForegroundColor Red
            $script:Failures.Add("$Name - $Details")
        }
        "WARN" {
            Write-Host "[WARN] $Name - $Details" -ForegroundColor Yellow
            $script:Warnings.Add("$Name - $Details")
        }
        "SKIP" { Write-Host "[SKIP] $Name - $Details" -ForegroundColor DarkYellow }
    }
}

function Convert-ToCommandLine {
    param(
        [string]$Command,
        [string[]]$Arguments
    )

    $rendered = foreach ($arg in $Arguments) {
        if ($arg -match '[\s"`$]') {
            '"' + ($arg -replace '"', '\"') + '"'
        }
        else {
            $arg
        }
    }

    return "$Command $($rendered -join ' ')"
}

function Invoke-Native {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [string]$Command,

        [Parameter()]
        [string[]]$Arguments = @(),

        [Parameter()]
        [string]$OutputFile = "",

        [Parameter()]
        [int[]]$AllowedExitCodes = @(0),

        [Parameter()]
        [string]$WorkingDirectory = $RepoPath,

        [Parameter()]
        [switch]$Quiet
    )

    $commandLine = Convert-ToCommandLine -Command $Command -Arguments $Arguments
    $script:CurrentCommand = $commandLine

    if (-not $Quiet) {
        Write-Host "> $commandLine" -ForegroundColor Cyan
    }

    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"

    try {
        Push-Location $WorkingDirectory

        $raw = & $Command @Arguments 2>&1
        $exitCode = $LASTEXITCODE

        $lines = @(
            $raw | ForEach-Object {
                if ($_ -is [System.Management.Automation.ErrorRecord]) {
                    $_.Exception.Message
                }
                else {
                    "$_"
                }
            }
        )
    }
    finally {
        Pop-Location
        $ErrorActionPreference = $previousPreference
    }

    if ($OutputFile) {
        $parent = Split-Path -Parent $OutputFile
        if ($parent -and -not (Test-Path -LiteralPath $parent)) {
            New-Item -ItemType Directory -Path $parent -Force | Out-Null
        }

        @(
            "COMMAND: $commandLine"
            "WORKDIR: $WorkingDirectory"
            "EXIT_CODE: $exitCode"
            ""
            $lines
        ) | Out-File -LiteralPath $OutputFile -Encoding utf8
    }

    if (-not $Quiet -and $lines.Count -gt 0) {
        $lines | ForEach-Object { Write-Host $_ }
    }

    return [PSCustomObject]@{
        Command  = $commandLine
        ExitCode = $exitCode
        Output   = $lines
        Success  = ($AllowedExitCodes -contains $exitCode)
    }
}

function Assert-CommandExists {
    param([string]$Name)

    $command = Get-Command $Name -ErrorAction SilentlyContinue
    if ($null -eq $command) {
        Add-Check -Name "Required command: $Name" -Status FAIL -Details "Command was not found in PATH."
        return $false
    }

    Add-Check -Name "Required command: $Name" -Status PASS -Details $command.Source
    return $true
}

function Get-DockerfileBaseImages {
    param([string]$Dockerfile)

    if (-not (Test-Path -LiteralPath $Dockerfile)) {
        return @()
    }

    $images = foreach ($line in Get-Content -LiteralPath $Dockerfile) {
        if ($line -match '^\s*FROM\s+([^\s]+)') {
            $Matches[1]
        }
    }

    return @($images)
}

function Get-FirstDockerfileBaseImage {
    param([string]$Dockerfile)

    $images = @(Get-DockerfileBaseImages -Dockerfile $Dockerfile)
    if ($images.Count -eq 0) {
        return ""
    }

    return $images[0]
}

function Test-DockerfileStandards {
    param(
        [string]$Service,
        [string]$Dockerfile,
        [string]$ProofDirectory
    )

    if (-not (Test-Path -LiteralPath $Dockerfile)) {
        Add-Check -Name "$Service Dockerfile exists" -Status FAIL -Details $Dockerfile
        return
    }

    Add-Check -Name "$Service Dockerfile exists" -Status PASS -Details $Dockerfile

    $content = Get-Content -LiteralPath $Dockerfile -Raw
    $baseImages = @(Get-DockerfileBaseImages -Dockerfile $Dockerfile)

    $baseImages |
        Out-File -LiteralPath (Join-Path $ProofDirectory "$Service-base-images.txt") -Encoding utf8

    if ($baseImages.Count -lt 2) {
        Add-Check -Name "$Service multi-stage build" -Status WARN `
            -Details "Only $($baseImages.Count) FROM instruction(s) found. Confirm this is intentional."
    }
    else {
        Add-Check -Name "$Service multi-stage build" -Status PASS `
            -Details "$($baseImages.Count) build/runtime stages found."
    }

    $unpinned = @($baseImages | Where-Object { $_ -notmatch '@sha256:[a-fA-F0-9]{64}$' })
    if ($unpinned.Count -gt 0) {
        Add-Check -Name "$Service base-image digest pinning" -Status FAIL `
            -Details ("Unpinned FROM image(s): " + ($unpinned -join ", "))
    }
    else {
        Add-Check -Name "$Service base-image digest pinning" -Status PASS `
            -Details "All FROM images are pinned to immutable SHA-256 digests."
    }

    # Evaluate only the final stage and the last USER instruction in that stage.
    # A Dockerfile may temporarily switch to root and then correctly return to appuser.
    $dockerfileLines = @(Get-Content -LiteralPath $Dockerfile)
    $fromIndexes = for ($i = 0; $i -lt $dockerfileLines.Count; $i++) {
        if ($dockerfileLines[$i] -match '^\s*FROM\s+') { $i }
    }

    $runtimeLines = if (@($fromIndexes).Count -gt 0) {
        $dockerfileLines[$fromIndexes[-1]..($dockerfileLines.Count - 1)]
    }
    else {
        $dockerfileLines
    }

    $runtimeUsers = @(
        $runtimeLines | ForEach-Object {
            if ($_ -match '^\s*USER\s+([^\s#]+)') { $Matches[1] }
        }
    )

    $finalRuntimeUser = if ($runtimeUsers.Count -gt 0) { $runtimeUsers[-1] } else { "" }

    if (-not $finalRuntimeUser) {
        Add-Check -Name "$Service non-root Dockerfile user" -Status FAIL `
            -Details "The final runtime stage has no USER instruction."
    }
    elseif ($finalRuntimeUser -match '^(?i:root|0|0:0)$') {
        Add-Check -Name "$Service non-root Dockerfile user" -Status FAIL `
            -Details "The final runtime user is $finalRuntimeUser."
    }
    else {
        Add-Check -Name "$Service non-root Dockerfile user" -Status PASS `
            -Details "Final runtime user is $finalRuntimeUser."
    }

    $misleadingRefs = @(
        $baseImages | Where-Object {
            $_ -match '^(?i:node:node|golang:golang|alpine:alpine)@sha256:'
        }
    )

    if ($misleadingRefs.Count -gt 0) {
        Add-Check -Name "$Service readable base-image references" -Status FAIL `
            -Details ("Malformed/redundant readable tag(s): " + ($misleadingRefs -join ", "))
    }
    else {
        Add-Check -Name "$Service readable base-image references" -Status PASS `
            -Details "Base-image names retain meaningful tags before the digest."
    }

    if ($content -match '(?im)^\s*HEALTHCHECK\s+') {
        Add-Check -Name "$Service Dockerfile healthcheck" -Status PASS `
            -Details "HEALTHCHECK instruction found."
    }
    else {
        Add-Check -Name "$Service Dockerfile healthcheck" -Status FAIL `
            -Details "No Dockerfile HEALTHCHECK found."
    }

    if ($content -match '(?im)^\s*(CMD|ENTRYPOINT)\s+\[') {
        Add-Check -Name "$Service exec-form startup" -Status PASS `
            -Details "JSON/exec-form CMD or ENTRYPOINT found."
    }
    else {
        Add-Check -Name "$Service exec-form startup" -Status WARN `
            -Details "No JSON/exec-form CMD or ENTRYPOINT detected."
    }

    if ($content -match '(?i)(AWS_SECRET_ACCESS_KEY|BEGIN PRIVATE KEY|password\s*=\s*["''][^"'']+|token\s*=\s*["''][^"'']+)') {
        Add-Check -Name "$Service Dockerfile secret scan" -Status FAIL `
            -Details "Potential credential or secret detected in Dockerfile."
    }
    else {
        Add-Check -Name "$Service Dockerfile secret scan" -Status PASS `
            -Details "No obvious embedded secret pattern detected."
    }
}

function Find-ComposeFile {
    param([string]$Root)

    $candidates = Get-ChildItem -LiteralPath $Root -Recurse -File -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -match '^(docker-)?compose.*\.ya?ml$' -and
            $_.Name -notmatch '(?i)override' -and
            $_.FullName -notmatch '(?i)[\\/](proof|\.git|\.trivy-cache|node_modules)[\\/]'
        } |
        Sort-Object `
            @{ Expression = { if ($_.FullName -match '(?i)[\\/]src[\\/]app[\\/]docker-compose\.ya?ml$') { 0 } else { 1 } } }, `
            FullName

    $matching = New-Object System.Collections.Generic.List[string]

    foreach ($candidate in $candidates) {
        $result = Invoke-Native `
            -Command "docker" `
            -Arguments @("compose", "-f", $candidate.FullName, "config", "--services") `
            -AllowedExitCodes @(0) `
            -Quiet

        if ($result.Success) {
            $services = @($result.Output | ForEach-Object { $_.Trim() } | Where-Object { $_ })
            if (($services -contains "checkout") -and ($services -contains "catalog")) {
                $matching.Add($candidate.FullName)
            }
        }
    }

    if ($matching.Count -eq 0) {
        return ""
    }

    if ($matching.Count -gt 1) {
        Add-Check -Name "Compose auto-detection" -Status WARN `
            -Details ("Multiple matching Compose files found. Using: " + $matching[0])
    }

    return $matching[0]
}

function Wait-ContainerHealth {
    param(
        [string]$ContainerId,
        [string]$ServiceName,
        [int]$TimeoutSeconds,
        [string]$OutputFile
    )

    $started = Get-Date
    $history = New-Object System.Collections.Generic.List[string]

    while (((Get-Date) - $started).TotalSeconds -lt $TimeoutSeconds) {
        $result = Invoke-Native `
            -Command "docker" `
            -Arguments @(
                "inspect",
                "--format",
                "{{.State.Running}}|{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}|{{.State.ExitCode}}",
                $ContainerId
            ) `
            -AllowedExitCodes @(0) `
            -Quiet

        $stateLine = if ($result.Output.Count -gt 0) {
            $result.Output[0].Trim()
        }
        else {
            "false|unknown|-1"
        }

        $parts = $stateLine -split '\|', 3
        $isRunning = ($parts[0] -eq "true")
        $status = if ($parts.Count -gt 1) { $parts[1] } else { "unknown" }
        $exitCode = if ($parts.Count -gt 2) { $parts[2] } else { "-1" }

        $history.Add("$((Get-Date).ToString('s')) running=$isRunning health=$status exitCode=$exitCode")

        if (-not $isRunning) {
            $history | Out-File -LiteralPath $OutputFile -Encoding utf8
            Add-Check -Name "$ServiceName container health" -Status FAIL `
                -Details "Container exited before becoming healthy. ExitCode=$exitCode."
            return $false
        }

        if ($status -eq "healthy") {
            $history | Out-File -LiteralPath $OutputFile -Encoding utf8
            Add-Check -Name "$ServiceName container health" -Status PASS `
                -Details "Container reached healthy state."
            return $true
        }

        if ($status -eq "unhealthy") {
            $history | Out-File -LiteralPath $OutputFile -Encoding utf8
            Add-Check -Name "$ServiceName container health" -Status FAIL `
                -Details "Container became unhealthy."
            return $false
        }

        if ($status -eq "none") {
            $history | Out-File -LiteralPath $OutputFile -Encoding utf8
            Add-Check -Name "$ServiceName container health" -Status FAIL `
                -Details "Image/container has no healthcheck."
            return $false
        }

        Start-Sleep -Seconds 5
    }

    $history | Out-File -LiteralPath $OutputFile -Encoding utf8
    Add-Check -Name "$ServiceName container health" -Status FAIL `
        -Details "Health did not become healthy within $TimeoutSeconds seconds."
    return $false
}

function Get-ContainerNetwork {
    param([string]$ContainerId)

    $result = Invoke-Native `
        -Command "docker" `
        -Arguments @(
            "inspect",
            "--format",
            "{{range `$key,`$value := .NetworkSettings.Networks}}{{`$key}}{{println}}{{end}}",
            $ContainerId
        ) `
        -AllowedExitCodes @(0) `
        -Quiet

    if (-not $result.Success) {
        return ""
    }

    $network = @($result.Output | Where-Object { $_.Trim() }) | Select-Object -First 1
    return "$network".Trim()
}

function Invoke-ContainerHttpCheck {
    param(
        [string]$Network,
        [string]$Url,
        [string]$Name,
        [string]$OutputFile,
        [int]$Attempts = 1
    )

    $allOutput = New-Object System.Collections.Generic.List[string]
    $passed = $true

    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        $result = Invoke-Native `
            -Command "docker" `
            -Arguments @(
                "run", "--rm",
                "--network", $Network,
                "curlimages/curl:8.12.1",
                "-fsS",
                "--max-time", "15",
                "-w", "`nHTTP_STATUS=%{http_code}`n",
                $Url
            ) `
            -AllowedExitCodes @(0) `
            -Quiet

        $allOutput.Add("ATTEMPT=$attempt")
        $allOutput.Add("EXIT_CODE=$($result.ExitCode)")
        $result.Output | ForEach-Object { $allOutput.Add($_) }
        $allOutput.Add("")

        if (-not $result.Success) {
            $passed = $false
        }

        if ($attempt -lt $Attempts) {
            Start-Sleep -Seconds 5
        }
    }

    $allOutput | Out-File -LiteralPath $OutputFile -Encoding utf8

    if ($passed) {
        Add-Check -Name $Name -Status PASS `
            -Details "$Attempts HTTP request(s) succeeded: $Url"
        return $true
    }

    Add-Check -Name $Name -Status FAIL `
        -Details "One or more HTTP requests failed: $Url"
    return $false
}

function Write-TroubleshootingCandidate {
    param(
        [string]$ProofDirectory,
        [string]$Issue,
        [string]$Context,
        [string]$Command,
        [string]$ErrorOutput,
        [string]$SuggestedValidation
    )

    $file = Join-Path $ProofDirectory "troubleshooting-candidates.md"

    @"
## $Issue

- **Date:** $((Get-Date).ToString("s"))
- **Status:** Open
- **Context:** $Context
- **Command:** ``$Command``
- **Observed output:** $ErrorOutput
- **Root cause:** Pending investigation
- **Fix:** Pending
- **Validation required:** $SuggestedValidation
- **Prevention:** Add after the root cause is confirmed.

"@ | Out-File -LiteralPath $file -Encoding utf8 -Append
}

function Save-ImageAudit {
    param(
        [string]$Service,
        [string]$Image,
        [string]$ProofDirectory
    )

    $inspectFile = Join-Path $ProofDirectory "$Service-image-inspect.json"
    $historyFile = Join-Path $ProofDirectory "$Service-image-history.txt"
    $configFile = Join-Path $ProofDirectory "$Service-image-config.txt"

    $inspect = Invoke-Native `
        -Command "docker" `
        -Arguments @("image", "inspect", $Image) `
        -OutputFile $inspectFile

    if (-not $inspect.Success) {
        Add-Check -Name "$Service image exists" -Status FAIL `
            -Details "docker image inspect failed for $Image"
        return $null
    }

    Add-Check -Name "$Service image exists" -Status PASS -Details $Image

    Invoke-Native `
        -Command "docker" `
        -Arguments @("history", "--no-trunc", $Image) `
        -OutputFile $historyFile |
        Out-Null

    $format = @'
User={{json .Config.User}}
WorkDir={{json .Config.WorkingDir}}
Entrypoint={{json .Config.Entrypoint}}
Cmd={{json .Config.Cmd}}
Healthcheck={{json .Config.Healthcheck}}
Labels={{json .Config.Labels}}
Architecture={{.Architecture}}
OS={{.Os}}
SizeBytes={{.Size}}
'@

    $config = Invoke-Native `
        -Command "docker" `
        -Arguments @("image", "inspect", "--format", $format, $Image) `
        -OutputFile $configFile

    $userLine = @($config.Output | Where-Object { $_ -match '^User=' }) | Select-Object -First 1
    $healthLine = @($config.Output | Where-Object { $_ -match '^Healthcheck=' }) | Select-Object -First 1
    $labelsLine = @($config.Output | Where-Object { $_ -match '^Labels=' }) | Select-Object -First 1

    if ("$userLine" -match 'User=(""|null|"root"|"0"|0)$') {
        Add-Check -Name "$Service image non-root user" -Status FAIL `
            -Details "Image user is empty, root, or UID 0."
    }
    else {
        Add-Check -Name "$Service image non-root user" -Status PASS `
            -Details "$userLine"
    }

    if ("$healthLine" -match 'Healthcheck=(null|<nil>|"")$') {
        Add-Check -Name "$Service image healthcheck" -Status FAIL `
            -Details "Image has no configured healthcheck."
    }
    elseif ("$healthLine" -match '(?i)CMD-SHELL.*(?:^|[\\"]|\s)CMD\s+(node|curl|wget|busybox)') {
        Add-Check -Name "$Service image healthcheck" -Status FAIL `
            -Details "Malformed healthcheck: an extra literal CMD appears inside CMD-SHELL. Use HEALTHCHECK ... CMD node/curl, not CMD CMD node/curl."
    }
    else {
        Add-Check -Name "$Service image healthcheck" -Status PASS `
            -Details "Image healthcheck is present and does not contain the known duplicate-CMD error."
    }

    if ("$labelsLine" -match 'Labels=(null|<nil>|"")$') {
        Add-Check -Name "$Service OCI labels" -Status FAIL `
            -Details "Image has no OCI labels."
    }
    elseif (("$labelsLine" -notmatch 'org\.opencontainers\.image\.title') -or
            ("$labelsLine" -notmatch 'org\.opencontainers\.image\.version') -or
            ("$labelsLine" -notmatch 'org\.opencontainers\.image\.revision') -or
            ("$labelsLine" -notmatch 'org\.opencontainers\.image\.created') -or
            ("$labelsLine" -notmatch 'org\.opencontainers\.image\.source')) {
        Add-Check -Name "$Service OCI labels" -Status FAIL `
            -Details "One or more required OCI labels are missing: title, version, revision, created, source."
    }
    else {
        Add-Check -Name "$Service OCI labels" -Status PASS `
            -Details "Required OCI labels are present."
    }

    $sizeResult = Invoke-Native `
        -Command "docker" `
        -Arguments @("image", "inspect", "--format", "{{.Size}}", $Image) `
        -AllowedExitCodes @(0) `
        -Quiet

    if ($sizeResult.Success -and $sizeResult.Output.Count -gt 0) {
        $bytes = [double]$sizeResult.Output[0]
        return [PSCustomObject]@{
            Service   = $Service
            Image     = $Image
            SizeBytes = [int64]$bytes
            SizeMiB   = [math]::Round($bytes / 1MB, 2)
        }
    }

    return $null
}

# --------------------------------------------------------------------------------------
# Initialization
# --------------------------------------------------------------------------------------

$RepoPath = (Resolve-Path -LiteralPath $RepoPath).Path
Set-Location $RepoPath

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$proofRelative = Join-Path "proof\day05" "pre-pr-$timestamp"
$ProofDirectory = Join-Path $RepoPath $proofRelative
New-Item -ItemType Directory -Path $ProofDirectory -Force | Out-Null

$transcriptFile = Join-Path $ProofDirectory "console-transcript.txt"
try {
    Start-Transcript -LiteralPath $transcriptFile -Force | Out-Null
}
catch {
    Write-Warning "Could not start transcript: $($_.Exception.Message)"
}

Write-Section "AWS Retail Day 05 - Automated Pre-PR Review v3"
Write-Host "Repository: $RepoPath"
Write-Host "Proof:      $ProofDirectory"
Write-Host "Timestamp:  $timestamp"

$Services = @(
    [PSCustomObject]@{
        Name       = "ui"
        Context    = "src\ui"
        Dockerfile = "src\ui\Dockerfile"
        Image      = "retail-ui:day05-pinned"
        Type       = "java"
    },
    [PSCustomObject]@{
        Name       = "cart"
        Context    = "src\cart"
        Dockerfile = "src\cart\Dockerfile"
        Image      = "retail-cart:day05-pinned"
        Type       = "java"
    },
    [PSCustomObject]@{
        Name       = "orders"
        Context    = "src\orders"
        Dockerfile = "src\orders\Dockerfile"
        Image      = "retail-orders:day05-pinned"
        Type       = "java"
    },
    [PSCustomObject]@{
        Name       = "checkout"
        Context    = "src\checkout"
        Dockerfile = "src\checkout\Dockerfile"
        Image      = "retail-checkout:day05-pinned"
        Type       = "node"
    },
    [PSCustomObject]@{
        Name       = "catalog"
        Context    = "src\catalog"
        Dockerfile = "src\catalog\Dockerfile"
        Image      = "retail-catalog:day05-pinned"
        Type       = "go"
    }
)

# --------------------------------------------------------------------------------------
# Required tools and repository preflight
# --------------------------------------------------------------------------------------

Write-Section "1. Required tools and repository preflight"

$gitAvailable = Assert-CommandExists -Name "git"
$dockerAvailable = Assert-CommandExists -Name "docker"

if (-not (Test-Path -LiteralPath (Join-Path $RepoPath ".git"))) {
    Add-Check -Name "Git repository root" -Status FAIL `
        -Details ".git was not found under $RepoPath"
}

if ($gitAvailable) {
    $repoRootResult = Invoke-Native `
        -Command "git" `
        -Arguments @("rev-parse", "--show-toplevel") `
        -OutputFile (Join-Path $ProofDirectory "git-repository-root.txt")

    if ($repoRootResult.Success) {
        $actualRoot = $repoRootResult.Output[0].Trim()
        if ([System.IO.Path]::GetFullPath($actualRoot) -eq [System.IO.Path]::GetFullPath($RepoPath)) {
            Add-Check -Name "Repository path" -Status PASS -Details $actualRoot
        }
        else {
            Add-Check -Name "Repository path" -Status FAIL `
                -Details "Expected $RepoPath but Git root is $actualRoot"
        }
    }

    if (-not $SkipFetch) {
        $fetch = Invoke-Native `
            -Command "git" `
            -Arguments @("fetch", "--all", "--prune") `
            -OutputFile (Join-Path $ProofDirectory "git-fetch.txt")

        if ($fetch.Success) {
            Add-Check -Name "Git fetch" -Status PASS -Details "Remote references refreshed."
        }
        else {
            Add-Check -Name "Git fetch" -Status WARN `
                -Details "Fetch failed. Review git-fetch.txt; local checks will continue."
        }
    }
    else {
        Add-Check -Name "Git fetch" -Status SKIP -Details "-SkipFetch was supplied."
    }

    $branch = Invoke-Native `
        -Command "git" `
        -Arguments @("branch", "--show-current") `
        -OutputFile (Join-Path $ProofDirectory "git-current-branch.txt")

    $branchName = if ($branch.Output.Count -gt 0) { $branch.Output[0].Trim() } else { "" }

    if (-not $branchName) {
        Add-Check -Name "Feature branch" -Status FAIL -Details "Detached HEAD or no branch name."
    }
    elseif ($branchName -in @("main", "master", $BaseBranch)) {
        Add-Check -Name "Feature branch" -Status FAIL `
            -Details "Current branch '$branchName' is a protected/base branch."
    }
    else {
        Add-Check -Name "Feature branch" -Status PASS -Details $branchName
    }

    $statusBefore = Invoke-Native `
        -Command "git" `
        -Arguments @("status", "--short", "--branch") `
        -OutputFile (Join-Path $ProofDirectory "git-status-before.txt")

    $statusText = $statusBefore.Output -join "`n"

    if ($statusText -match '(?m)^\?\?\s+\.trivy-cache/') {
        Add-Check -Name "Untracked Trivy cache" -Status FAIL `
            -Details ".trivy-cache/ is untracked. Remove it or add it to .gitignore before the PR."
    }
    else {
        Add-Check -Name "Untracked Trivy cache" -Status PASS `
            -Details "No untracked .trivy-cache/ directory detected."
    }

    if ($statusText -match '(?m)^\?\?.*(t-Path|\.gitattributes.*[^\x00-\x7F])') {
        Add-Check -Name "Suspicious accidental filename" -Status FAIL `
            -Details "A malformed/unexpected untracked filename was detected. Inspect and remove it only after confirming it is accidental."
    }
    else {
        Add-Check -Name "Suspicious accidental filename" -Status PASS `
            -Details "No known malformed filename pattern detected."
    }

    $diffCheck = Invoke-Native `
        -Command "git" `
        -Arguments @("diff", "--check") `
        -OutputFile (Join-Path $ProofDirectory "git-diff-check.txt")

    if ($diffCheck.Success) {
        Add-Check -Name "Git whitespace check" -Status PASS -Details "git diff --check returned exit code 0."
    }
    else {
        Add-Check -Name "Git whitespace check" -Status FAIL `
            -Details "Whitespace errors found. See git-diff-check.txt."
    }

    Invoke-Native `
        -Command "git" `
        -Arguments @("diff", "--stat") `
        -OutputFile (Join-Path $ProofDirectory "git-diff-stat.txt") |
        Out-Null

    $diff = Invoke-Native `
        -Command "git" `
        -Arguments @("diff", "--no-ext-diff", "--binary") `
        -OutputFile (Join-Path $ProofDirectory "git-diff.patch")

    $diffText = $diff.Output -join "`n"

    $placeholderPattern = '(?i)(YOUR_TAG|ACTUAL_DIGEST|ACTUAL_NETWORK|<IMAGE>|<NETWORK>|<PORT>|<HEALTH_PATH>|<PRODUCTS_PATH>|REPLACE_ME)'
    if ($diffText -match $placeholderPattern) {
        Add-Check -Name "Placeholder scan" -Status FAIL `
            -Details "Unresolved placeholder found in Git diff."
    }
    else {
        Add-Check -Name "Placeholder scan" -Status PASS `
            -Details "No known unresolved placeholder found."
    }

    $secretPattern = '(?i)(BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY|AWS_SECRET_ACCESS_KEY\s*[=:]\s*(?!test\b)\S+|password\s*[=:]\s*["''][^"'']{4,}|token\s*[=:]\s*["''][^"'']{8,})'
    if ($diffText -match $secretPattern) {
        Add-Check -Name "Potential secret scan" -Status FAIL `
            -Details "Potential credential pattern found in Git diff."
    }
    else {
        Add-Check -Name "Potential secret scan" -Status PASS `
            -Details "No obvious credential pattern found in Git diff."
    }

    $baseRef = ""
    $remoteBase = "origin/$BaseBranch"

    $remoteCheck = Invoke-Native `
        -Command "git" `
        -Arguments @("rev-parse", "--verify", $remoteBase) `
        -AllowedExitCodes @(0, 128) `
        -Quiet

    if ($remoteCheck.ExitCode -eq 0) {
        $baseRef = $remoteBase
    }
    else {
        $localCheck = Invoke-Native `
            -Command "git" `
            -Arguments @("rev-parse", "--verify", $BaseBranch) `
            -AllowedExitCodes @(0, 128) `
            -Quiet

        if ($localCheck.ExitCode -eq 0) {
            $baseRef = $BaseBranch
        }
    }

    if ($baseRef) {
        Invoke-Native `
            -Command "git" `
            -Arguments @("diff", "--name-status", "$baseRef...HEAD") `
            -OutputFile (Join-Path $ProofDirectory "pr-files-against-$BaseBranch.txt") |
            Out-Null

        Invoke-Native `
            -Command "git" `
            -Arguments @("diff", "--stat", "$baseRef...HEAD") `
            -OutputFile (Join-Path $ProofDirectory "pr-stat-against-$BaseBranch.txt") |
            Out-Null

        Invoke-Native `
            -Command "git" `
            -Arguments @("rev-list", "--left-right", "--count", "$baseRef...HEAD") `
            -OutputFile (Join-Path $ProofDirectory "branch-divergence.txt") |
            Out-Null

        Add-Check -Name "PR base comparison" -Status PASS -Details "Compared HEAD against $baseRef."
    }
    else {
        Add-Check -Name "PR base comparison" -Status WARN `
            -Details "Could not resolve base branch '$BaseBranch'."
    }
}

if ($dockerAvailable) {
    $dockerOs = Invoke-Native `
        -Command "docker" `
        -Arguments @("info", "--format", "{{.OSType}}") `
        -OutputFile (Join-Path $ProofDirectory "docker-os-type.txt")

    if ($dockerOs.Success -and $dockerOs.Output.Count -gt 0 -and $dockerOs.Output[0].Trim() -eq "linux") {
        Add-Check -Name "Docker Linux engine" -Status PASS -Details "Docker OSType=linux"
    }
    else {
        Add-Check -Name "Docker Linux engine" -Status FAIL `
            -Details "Docker Desktop is unavailable or not using Linux containers."
    }

    $composeVersion = Invoke-Native `
        -Command "docker" `
        -Arguments @("compose", "version") `
        -OutputFile (Join-Path $ProofDirectory "docker-compose-version.txt")

    if ($composeVersion.Success) {
        Add-Check -Name "Docker Compose v2" -Status PASS `
            -Details ($composeVersion.Output -join " ")
    }
    else {
        Add-Check -Name "Docker Compose v2" -Status FAIL `
            -Details "docker compose command failed."
    }
}

# --------------------------------------------------------------------------------------
# Dockerfile standards
# --------------------------------------------------------------------------------------

Write-Section "2. Dockerfile standards and static validation"

foreach ($service in $Services) {
    Test-DockerfileStandards `
        -Service $service.Name `
        -Dockerfile (Join-Path $RepoPath $service.Dockerfile) `
        -ProofDirectory $ProofDirectory
}

# --------------------------------------------------------------------------------------
# Source verification
# --------------------------------------------------------------------------------------

Write-Section "3. Source verification inside containers"

$mavenImage = "maven:3.9.16-eclipse-temurin-21"
$mavenCache = "retail-maven-cache"

if (-not $SkipJavaVerify) {
    $javaServices = @(
        [PSCustomObject]@{
            Name = "ui"
            Args = @()
            MavenArgs = @("mvn", "--batch-mode", "--no-transfer-progress", "clean", "verify")
        },
        [PSCustomObject]@{
            Name = "cart"
            Args = @(
                "--mount", "type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock",
                "--env", "TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal",
                "--env", "AWS_ACCESS_KEY_ID=test",
                "--env", "AWS_SECRET_ACCESS_KEY=test",
                "--env", "AWS_REGION=us-east-1"
            )
            MavenArgs = @("mvn", "--batch-mode", "--no-transfer-progress", "clean", "verify")
        },
        [PSCustomObject]@{
            Name = "orders"
            Args = @(
                "--mount", "type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock",
                "--env", "TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal"
            )
            MavenArgs = @(
                "mvn",
                "--batch-mode",
                "--no-transfer-progress",
                "-Dspring-boot.start.maxAttempts=180",
                "clean",
                "verify"
            )
        }
    )

    foreach ($item in $javaServices) {
        $sourcePath = (Resolve-Path -LiteralPath (Join-Path $RepoPath "src\$($item.Name)")).Path
        $arguments = @(
            "run", "--rm",
            "--mount", "type=bind,src=$sourcePath,dst=/workspace",
            "--mount", "type=volume,src=$mavenCache,dst=/root/.m2"
        ) + $item.Args + @(
            "--workdir", "/workspace",
            $mavenImage
        ) + $item.MavenArgs

        $result = Invoke-Native `
            -Command "docker" `
            -Arguments $arguments `
            -OutputFile (Join-Path $ProofDirectory "$($item.Name)-maven-clean-verify.txt")

        if ($result.Success) {
            Add-Check -Name "$($item.Name) Maven clean verify" -Status PASS `
                -Details "Containerized Maven verification succeeded."
        }
        else {
            Add-Check -Name "$($item.Name) Maven clean verify" -Status FAIL `
                -Details "See $($item.Name)-maven-clean-verify.txt."

            Write-TroubleshootingCandidate `
                -ProofDirectory $ProofDirectory `
                -Issue "$($item.Name) Maven clean verify failed" `
                -Context "Day 05 dependency and source verification." `
                -Command $result.Command `
                -ErrorOutput "Exit code $($result.ExitCode). See $($item.Name)-maven-clean-verify.txt." `
                -SuggestedValidation "The same clean verify command must exit 0 and create the service JAR."
        }
    }
}
else {
    foreach ($name in @("ui", "cart", "orders")) {
        Add-Check -Name "$name Maven clean verify" -Status SKIP `
            -Details "-SkipJavaVerify was supplied."
    }
}

# Checkout Node verification
# The Docker build stage is the clean, reproducible source-build test.
# Avoid bind-mounting node_modules from a named volume on top of a Windows bind mount;
# that combination produced an npm internal error and a false negative.
$checkoutPath = (Resolve-Path -LiteralPath (Join-Path $RepoPath "src\checkout")).Path
$checkoutSourceImage = "retail-checkout:day05-source-verify-$timestamp"

$checkoutSource = Invoke-Native `
    -Command "docker" `
    -Arguments @(
        "build",
        "--target", "build",
        "--tag", $checkoutSourceImage,
        $checkoutPath
    ) `
    -OutputFile (Join-Path $ProofDirectory "checkout-npm-ci-build.txt")

if ($checkoutSource.Success) {
    Add-Check -Name "Checkout npm ci and build" -Status PASS `
        -Details "Docker build target 'build' completed npm ci and npm run build successfully."
}
else {
    Add-Check -Name "Checkout npm ci and build" -Status FAIL `
        -Details "See checkout-npm-ci-build.txt."

    Write-TroubleshootingCandidate `
        -ProofDirectory $ProofDirectory `
        -Issue "Checkout npm ci or build failed" `
        -Context "Day 05 Checkout clean source verification through the Docker build stage." `
        -Command $checkoutSource.Command `
        -ErrorOutput "Exit code $($checkoutSource.ExitCode). See checkout-npm-ci-build.txt." `
        -SuggestedValidation "The Docker build target named build must exit 0 after npm ci and npm run build."
}

Invoke-Native `
    -Command "docker" `
    -Arguments @("image", "rm", "-f", $checkoutSourceImage) `
    -AllowedExitCodes @(0, 1) `
    -Quiet |
    Out-Null

# Catalog Go verification
$catalogDockerfile = Join-Path $RepoPath "src\catalog\Dockerfile"
$goImage = Get-FirstDockerfileBaseImage -Dockerfile $catalogDockerfile

if (-not $goImage) {
    Add-Check -Name "Catalog Go build image detection" -Status FAIL `
        -Details "Could not detect the first FROM image in Catalog Dockerfile."
}
else {
    $catalogPath = (Resolve-Path -LiteralPath (Join-Path $RepoPath "src\catalog")).Path
    $goModCache = "retail-go-mod-cache"
    $goBuildCache = "retail-go-build-cache"

    $catalogSource = Invoke-Native `
        -Command "docker" `
        -Arguments @(
            "run", "--rm",
            "--mount", "type=bind,src=$catalogPath,dst=/workspace,readonly",
            "--mount", "type=volume,src=$goModCache,dst=/go/pkg/mod",
            "--mount", "type=volume,src=$goBuildCache,dst=/root/.cache/go-build",
            "--mount", "type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock",
            "--env", "TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal",
            "--env", "TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock",
            "--workdir", "/workspace",
            $goImage,
            "sh", "-c",
            'export PATH="/usr/local/go/bin:/go/bin:$PATH"; go version && go env GOOS GOARCH CGO_ENABLED && go test ./... && go build -o /tmp/catalog-main .'
        ) `
        -OutputFile (Join-Path $ProofDirectory "catalog-go-test-build.txt")

    if ($catalogSource.Success) {
        Add-Check -Name "Catalog Go test and build" -Status PASS `
            -Details "go test ./... and go build ./... succeeded."
    }
    else {
        Add-Check -Name "Catalog Go test and build" -Status FAIL `
            -Details "See catalog-go-test-build.txt."

        Write-TroubleshootingCandidate `
            -ProofDirectory $ProofDirectory `
            -Issue "Catalog Go test or build failed" `
            -Context "Day 05 Catalog source verification and CGO validation." `
            -Command $catalogSource.Command `
            -ErrorOutput "Exit code $($catalogSource.ExitCode). See catalog-go-test-build.txt." `
            -SuggestedValidation "go test ./... and go build ./... must exit 0; record GOOS, GOARCH, and CGO_ENABLED."
    }
}

# --------------------------------------------------------------------------------------
# Build all Day 05 images
# --------------------------------------------------------------------------------------

Write-Section "4. Build all Day 05 images"

$buildVersion = "day05-pinned"
$revisionResult = Invoke-Native `
    -Command "git" `
    -Arguments @("rev-parse", "--short", "HEAD") `
    -AllowedExitCodes @(0) `
    -Quiet

$buildRevision = if ($revisionResult.Success -and $revisionResult.Output.Count -gt 0) {
    $revisionResult.Output[0].Trim()
}
else {
    "unknown"
}

$buildDate = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")

foreach ($service in $Services) {
    $contextPath = Join-Path $RepoPath $service.Context

    $build = Invoke-Native `
        -Command "docker" `
        -Arguments @(
            "build",
            "--pull=false",
            "--build-arg", "BUILD_VERSION=$buildVersion",
            "--build-arg", "BUILD_REVISION=$buildRevision",
            "--build-arg", "BUILD_DATE=$buildDate",
            "--tag", $service.Image,
            $contextPath
        ) `
        -OutputFile (Join-Path $ProofDirectory "$($service.Name)-docker-build.txt")

    if ($build.Success) {
        Add-Check -Name "$($service.Name) Docker image build" -Status PASS `
            -Details $service.Image
    }
    else {
        Add-Check -Name "$($service.Name) Docker image build" -Status FAIL `
            -Details "See $($service.Name)-docker-build.txt."

        Write-TroubleshootingCandidate `
            -ProofDirectory $ProofDirectory `
            -Issue "$($service.Name) Docker image build failed" `
            -Context "Day 05 hardened image build." `
            -Command $build.Command `
            -ErrorOutput "Exit code $($build.ExitCode). See $($service.Name)-docker-build.txt." `
            -SuggestedValidation "docker build must exit 0 and docker image inspect must find $($service.Image)."
    }
}

# --------------------------------------------------------------------------------------
# Image inspection, history, size, and configuration
# --------------------------------------------------------------------------------------

Write-Section "5. Image inspection, history, and size report"

$imageSizes = New-Object System.Collections.Generic.List[object]

foreach ($service in $Services) {
    $size = Save-ImageAudit `
        -Service $service.Name `
        -Image $service.Image `
        -ProofDirectory $ProofDirectory

    if ($null -ne $size) {
        $imageSizes.Add($size)
    }
}

$imageSizes |
    Export-Csv -LiteralPath (Join-Path $ProofDirectory "image-sizes.csv") `
    -NoTypeInformation `
    -Encoding utf8

$imageSizes |
    Format-Table -AutoSize |
    Out-String |
    Out-File -LiteralPath (Join-Path $ProofDirectory "image-sizes.txt") -Encoding utf8

# --------------------------------------------------------------------------------------
# Optional Trivy scans
# --------------------------------------------------------------------------------------

Write-Section "6. Optional Trivy scans"

if ($RunTrivy) {
    $trivyImage = "aquasec/trivy:0.72.0"
    $proofUnix = $ProofDirectory

    foreach ($service in $Services) {
        $reportName = "$($service.Name)-trivy.json"

        $trivy = Invoke-Native `
            -Command "docker" `
            -Arguments @(
                "run", "--rm",
                "--mount", "type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock",
                "--mount", "type=volume,src=retail-trivy-cache,dst=/root/.cache",
                "--mount", "type=bind,src=$proofUnix,dst=/reports",
                $trivyImage,
                "image",
                "--format", "json",
                "--output", "/reports/$reportName",
                $service.Image
            ) `
            -OutputFile (Join-Path $ProofDirectory "$($service.Name)-trivy-console.txt")

        $reportPath = Join-Path $ProofDirectory $reportName
        if ($trivy.Success -and (Test-Path -LiteralPath $reportPath) -and ((Get-Item $reportPath).Length -gt 0)) {
            Add-Check -Name "$($service.Name) Trivy scan" -Status PASS `
                -Details "$reportName created."
        }
        else {
            Add-Check -Name "$($service.Name) Trivy scan" -Status FAIL `
                -Details "Scan failed or JSON report is empty."
        }
    }
}
else {
    Add-Check -Name "Trivy scans" -Status SKIP `
        -Details "Dependency/security rescanning is deferred unless -RunTrivy is supplied."
}

# --------------------------------------------------------------------------------------
# Compose runtime and security validation
# --------------------------------------------------------------------------------------

Write-Section "7. Runtime, health, functional, and security validation"

$resolvedComposeFile = ""

if (-not $SkipRuntime) {
    if ($ComposeFile) {
        $candidate = if ([System.IO.Path]::IsPathRooted($ComposeFile)) {
            $ComposeFile
        }
        else {
            Join-Path $RepoPath $ComposeFile
        }

        if (Test-Path -LiteralPath $candidate) {
            $resolvedComposeFile = (Resolve-Path -LiteralPath $candidate).Path
        }
        else {
            Add-Check -Name "Compose file" -Status FAIL `
                -Details "Specified Compose file does not exist: $candidate"
        }
    }
    else {
        $resolvedComposeFile = Find-ComposeFile -Root $RepoPath
    }

    if (-not $resolvedComposeFile) {
        Add-Check -Name "Compose file" -Status FAIL `
            -Details "Could not find a Compose file containing checkout and catalog services. Pass -ComposeFile."
    }
    else {
        Add-Check -Name "Compose file" -Status PASS -Details $resolvedComposeFile

        $composeServices = Invoke-Native `
            -Command "docker" `
            -Arguments @("compose", "-f", $resolvedComposeFile, "config", "--services") `
            -OutputFile (Join-Path $ProofDirectory "compose-services.txt")

        $serviceNames = @($composeServices.Output | ForEach-Object { $_.Trim() } | Where-Object { $_ })

        foreach ($requiredService in @("checkout", "catalog")) {
            if ($serviceNames -contains $requiredService) {
                Add-Check -Name "Compose service: $requiredService" -Status PASS `
                    -Details "Service exists in Compose configuration."
            }
            else {
                Add-Check -Name "Compose service: $requiredService" -Status FAIL `
                    -Details "Required service not found."
            }
        }

        $overridePath = Join-Path $ProofDirectory "compose.day05.audit.override.yml"

        # Override only the image tags. Runtime hardening must come from the
        # project's real Compose configuration, then the script inspects and validates it.
        # This avoids duplicate list entries such as security_opt during Compose merge.
        @"
services:
  checkout:
    image: retail-checkout:day05-pinned
    ports: !reset []

  catalog:
    image: retail-catalog:day05-pinned
    ports: !reset []

  checkout-redis:
    ports: !reset []

  catalog-db:
    ports: !reset []

  opensearch:
    ports: !reset []
"@ | Out-File -LiteralPath $overridePath -Encoding utf8

        $composeProject = "retailday05audit$($timestamp -replace '-', '')"
        $composeBaseArgs = @(
            "compose",
            "-p", $composeProject,
            "-f", $resolvedComposeFile,
            "-f", $overridePath
        )

        $up = Invoke-Native `
            -Command "docker" `
            -Arguments ($composeBaseArgs + @(
                "up", "-d", "--no-build",
                "checkout", "catalog"
            )) `
            -OutputFile (Join-Path $ProofDirectory "compose-up.txt")

        if (-not $up.Success) {
            Add-Check -Name "Compose runtime startup" -Status FAIL `
                -Details "docker compose up failed. See compose-up.txt."

            Write-TroubleshootingCandidate `
                -ProofDirectory $ProofDirectory `
                -Issue "Day 05 Compose runtime startup failed" `
                -Context "Starting Checkout, Catalog, and their Compose dependencies with hardened runtime settings." `
                -Command $up.Command `
                -ErrorOutput "Exit code $($up.ExitCode). See compose-up.txt." `
                -SuggestedValidation "docker compose up must exit 0 and both services must become healthy."
        }
        else {
            Add-Check -Name "Compose runtime startup" -Status PASS `
                -Details "Checkout and Catalog were started with dependencies."

            $containerMap = @{}

            foreach ($serviceName in @("checkout", "catalog")) {
                $idResult = Invoke-Native `
                    -Command "docker" `
                    -Arguments ($composeBaseArgs + @("ps", "-aq", $serviceName)) `
                    -AllowedExitCodes @(0) `
                    -Quiet

                $containerId = if ($idResult.Output.Count -gt 0) {
                    $idResult.Output[0].Trim()
                }
                else {
                    ""
                }

                if (-not $containerId) {
                    Add-Check -Name "$serviceName runtime container" -Status FAIL `
                        -Details "Compose did not return a container ID."
                    continue
                }

                $containerMap[$serviceName] = $containerId

                Invoke-Native `
                    -Command "docker" `
                    -Arguments @("inspect", $containerId) `
                    -OutputFile (Join-Path $ProofDirectory "$serviceName-container-inspect.json") |
                    Out-Null

                Invoke-Native `
                    -Command "docker" `
                    -Arguments @("logs", "--timestamps", $containerId) `
                    -AllowedExitCodes @(0, 1) `
                    -OutputFile (Join-Path $ProofDirectory "$serviceName-container-logs.txt") |
                    Out-Null

                $securityFormat = @'
User={{json .Config.User}}
ReadonlyRootfs={{json .HostConfig.ReadonlyRootfs}}
CapDrop={{json .HostConfig.CapDrop}}
SecurityOpt={{json .HostConfig.SecurityOpt}}
RestartCount={{.RestartCount}}
OOMKilled={{.State.OOMKilled}}
'@

                $security = Invoke-Native `
                    -Command "docker" `
                    -Arguments @("inspect", "--format", $securityFormat, $containerId) `
                    -OutputFile (Join-Path $ProofDirectory "$serviceName-runtime-security.txt")

                $securityText = $security.Output -join "`n"

                if ($securityText -match 'ReadonlyRootfs=true') {
                    Add-Check -Name "$serviceName read-only root filesystem" -Status PASS `
                        -Details "ReadonlyRootfs=true"
                }
                else {
                    Add-Check -Name "$serviceName read-only root filesystem" -Status FAIL `
                        -Details "ReadonlyRootfs is not true."
                }

                if ($securityText -match '(?i)CapDrop=.*ALL') {
                    Add-Check -Name "$serviceName dropped capabilities" -Status PASS `
                        -Details "ALL capabilities are dropped."
                }
                else {
                    Add-Check -Name "$serviceName dropped capabilities" -Status FAIL `
                        -Details "CapDrop does not contain ALL."
                }

                if ($securityText -match '(?i)no-new-privileges') {
                    Add-Check -Name "$serviceName no-new-privileges" -Status PASS `
                        -Details "no-new-privileges is enabled."
                }
                else {
                    Add-Check -Name "$serviceName no-new-privileges" -Status FAIL `
                        -Details "no-new-privileges was not found."
                }

                Wait-ContainerHealth `
                    -ContainerId $containerId `
                    -ServiceName $serviceName `
                    -TimeoutSeconds $HealthTimeoutSeconds `
                    -OutputFile (Join-Path $ProofDirectory "$serviceName-health-history.txt") |
                    Out-Null
            }

            if ($containerMap.ContainsKey("checkout")) {
                $checkoutNetwork = Get-ContainerNetwork -ContainerId $containerMap["checkout"]

                if ($checkoutNetwork) {
                    Invoke-ContainerHttpCheck `
                        -Network $checkoutNetwork `
                        -Url "http://checkout:$InternalPort$CheckoutHealthPath" `
                        -Name "Checkout repeated health endpoint" `
                        -OutputFile (Join-Path $ProofDirectory "checkout-http-health.txt") `
                        -Attempts 5 |
                        Out-Null
                }
                else {
                    Add-Check -Name "Checkout network discovery" -Status FAIL `
                        -Details "Could not determine the Compose network."
                }
            }

            if ($containerMap.ContainsKey("catalog")) {
                $catalogNetwork = Get-ContainerNetwork -ContainerId $containerMap["catalog"]

                if ($catalogNetwork) {
                    Invoke-ContainerHttpCheck `
                        -Network $catalogNetwork `
                        -Url "http://catalog:$InternalPort$CatalogHealthPath" `
                        -Name "Catalog health endpoint" `
                        -OutputFile (Join-Path $ProofDirectory "catalog-http-health.txt") `
                        -Attempts 3 |
                        Out-Null

                    if ([string]::IsNullOrWhiteSpace($CatalogFunctionalPath)) {
                        Add-Check `
                            -Name "Catalog functional endpoint" `
                            -Status SKIP `
                            -Details "No Catalog business endpoint was configured. Health, dependency startup, migration, and container security checks remain required."
                    }
                    else {
                        $normalizedCatalogFunctionalPath = $CatalogFunctionalPath.Trim()

                        if (-not $normalizedCatalogFunctionalPath.StartsWith("/")) {
                            $normalizedCatalogFunctionalPath = "/$normalizedCatalogFunctionalPath"
                        }

                        Invoke-ContainerHttpCheck `
                            -Network $catalogNetwork `
                            -Url "http://catalog:$InternalPort$normalizedCatalogFunctionalPath" `
                            -Name "Catalog functional endpoint" `
                            -OutputFile (Join-Path $ProofDirectory "catalog-http-functional.txt") `
                            -Attempts 1 |
                            Out-Null
                    }
                }
                else {
                    Add-Check -Name "Catalog network discovery" -Status FAIL `
                        -Details "Could not determine the Compose network."
                }
            }

            $stopStarted = Get-Date

            $stop = Invoke-Native `
                -Command "docker" `
                -Arguments ($composeBaseArgs + @("stop", "-t", "30", "checkout", "catalog")) `
                -OutputFile (Join-Path $ProofDirectory "compose-graceful-stop.txt")

            $stopSeconds = [math]::Round(((Get-Date) - $stopStarted).TotalSeconds, 2)
            "StopDurationSeconds=$stopSeconds" |
                Out-File -LiteralPath (Join-Path $ProofDirectory "graceful-stop-duration.txt") -Encoding utf8

            if ($stop.Success -and $stopSeconds -lt 35) {
                Add-Check -Name "Graceful shutdown" -Status PASS `
                    -Details "Compose stop completed in $stopSeconds seconds."
            }
            else {
                Add-Check -Name "Graceful shutdown" -Status FAIL `
                    -Details "Stop failed or exceeded expected duration: $stopSeconds seconds."
            }

            foreach ($serviceName in @("checkout", "catalog")) {
                if ($containerMap.ContainsKey($serviceName)) {
                    Invoke-Native `
                        -Command "docker" `
                        -Arguments @(
                            "inspect",
                            "--format",
                            "ExitCode={{.State.ExitCode}} OOMKilled={{.State.OOMKilled}} Error={{json .State.Error}}",
                            $containerMap[$serviceName]
                        ) `
                        -AllowedExitCodes @(0) `
                        -OutputFile (Join-Path $ProofDirectory "$serviceName-stop-state.txt") |
                        Out-Null
                }
            }
        }

        Invoke-Native `
            -Command "docker" `
            -Arguments ($composeBaseArgs + @("down", "--remove-orphans")) `
            -AllowedExitCodes @(0, 1) `
            -OutputFile (Join-Path $ProofDirectory "compose-down.txt") |
            Out-Null
    }
}
else {
    Add-Check -Name "Runtime validation" -Status SKIP `
        -Details "-SkipRuntime was supplied."
}

# --------------------------------------------------------------------------------------
# Staging and cached-diff review
# --------------------------------------------------------------------------------------

Write-Section "8. Staging and cached-diff review"

if ($CommitChanges -and -not $StageChanges) {
    Add-Check -Name "Commit option" -Status FAIL `
        -Details "-CommitChanges requires -StageChanges."
}

if ($StageChanges) {
    if ($script:Failures.Count -gt 0) {
        Add-Check -Name "Git staging" -Status SKIP `
            -Details "Checks failed; no files were staged."
    }
    else {
        $stagePaths = @(
            "src/ui",
            "src/cart",
            "src/orders",
            "src/checkout",
            "src/catalog",
            $proofRelative
        )

        $stage = Invoke-Native `
            -Command "git" `
            -Arguments (@("add", "-A", "--") + $stagePaths) `
            -OutputFile (Join-Path $ProofDirectory "git-add.txt")

        if ($stage.Success) {
            Add-Check -Name "Git staging" -Status PASS `
                -Details "Day 05 paths were staged."
        }
        else {
            Add-Check -Name "Git staging" -Status FAIL `
                -Details "git add failed."
        }
    }
}
else {
    Add-Check -Name "Git staging" -Status SKIP `
        -Details "Use -StageChanges after reviewing the generated report."
}

$cachedNames = Invoke-Native `
    -Command "git" `
    -Arguments @("diff", "--cached", "--name-status") `
    -OutputFile (Join-Path $ProofDirectory "git-cached-name-status.txt")

$cachedCheck = Invoke-Native `
    -Command "git" `
    -Arguments @("diff", "--cached", "--check") `
    -OutputFile (Join-Path $ProofDirectory "git-cached-check.txt")

Invoke-Native `
    -Command "git" `
    -Arguments @("diff", "--cached", "--stat") `
    -OutputFile (Join-Path $ProofDirectory "git-cached-stat.txt") |
    Out-Null

Invoke-Native `
    -Command "git" `
    -Arguments @("diff", "--cached", "--no-ext-diff", "--binary") `
    -OutputFile (Join-Path $ProofDirectory "git-cached-diff.patch") |
    Out-Null

if ($cachedCheck.Success) {
    Add-Check -Name "Cached Git whitespace check" -Status PASS `
        -Details "git diff --cached --check returned exit code 0."
}
else {
    Add-Check -Name "Cached Git whitespace check" -Status FAIL `
        -Details "Staged content contains whitespace errors."
}

if ($CommitChanges) {
    if ($script:Failures.Count -gt 0) {
        Add-Check -Name "Git commit" -Status SKIP `
            -Details "Checks failed; commit was not created."
    }
    elseif ($cachedNames.Output.Count -eq 0) {
        Add-Check -Name "Git commit" -Status FAIL `
            -Details "No staged changes are available to commit."
    }
    else {
        $commit = Invoke-Native `
            -Command "git" `
            -Arguments @("commit", "-m", $CommitMessage) `
            -OutputFile (Join-Path $ProofDirectory "git-commit.txt")

        if ($commit.Success) {
            Add-Check -Name "Git commit" -Status PASS -Details $CommitMessage
        }
        else {
            Add-Check -Name "Git commit" -Status FAIL `
                -Details "git commit failed."
        }
    }
}
else {
    Add-Check -Name "Git commit" -Status SKIP `
        -Details "Use -CommitChanges only after the report passes."
}

# --------------------------------------------------------------------------------------
# Final Git state and summary
# --------------------------------------------------------------------------------------

Write-Section "9. Final Git state and review summary"

Invoke-Native `
    -Command "git" `
    -Arguments @("status", "--short", "--branch") `
    -OutputFile (Join-Path $ProofDirectory "git-status-final.txt") |
    Out-Null

Invoke-Native `
    -Command "git" `
    -Arguments @("log", "-1", "--pretty=format:%H%n%h%n%s%n%an%n%ad") `
    -OutputFile (Join-Path $ProofDirectory "git-last-commit.txt") |
    Out-Null

$summaryCsv = Join-Path $ProofDirectory "pre-pr-checks.csv"
$script:Checks |
    Export-Csv -LiteralPath $summaryCsv -NoTypeInformation -Encoding utf8

$summaryJson = Join-Path $ProofDirectory "pre-pr-summary.json"

# Windows PowerShell 5.1 can throw "Argument types do not match" when a generic
# List[T] is embedded directly inside a PSCustomObject. Materialize plain arrays first.
$failureItems = @($script:Failures | ForEach-Object { [string]$_ })
$warningItems = @($script:Warnings | ForEach-Object { [string]$_ })
$checkItems = @(
    $script:Checks | ForEach-Object {
        [PSCustomObject]@{
            Time    = [string]$_.Time
            Check   = [string]$_.Check
            Status  = [string]$_.Status
            Details = [string]$_.Details
        }
    }
)

$summaryObject = [PSCustomObject]@{
    GeneratedAt         = (Get-Date).ToString("s")
    Repository          = $RepoPath
    ProofDirectory      = $ProofDirectory
    FailureCount        = [int]$script:Failures.Count
    WarningCount        = [int]$script:Warnings.Count
    Failures            = $failureItems
    Warnings            = $warningItems
    Checks              = $checkItems
    ReadyForPullRequest = [bool]($script:Failures.Count -eq 0)
}

$summaryObject |
    ConvertTo-Json -Depth 8 |
    Out-File -LiteralPath $summaryJson -Encoding utf8

$summaryMarkdown = Join-Path $ProofDirectory "PRE-PR-REVIEW.md"

$statusWord = if ($script:Failures.Count -eq 0) { "READY" } else { "NOT READY" }

$checkTable = $script:Checks | ForEach-Object {
    "| $($_.Status) | $($_.Check -replace '\|', '/') | $($_.Details -replace '\|', '/') |"
}

@"
# AWS Retail Day 05 — Pre-PR Review

- **Generated:** $((Get-Date).ToString("s"))
- **Repository:** ``$RepoPath``
- **Status:** **$statusWord**
- **Failures:** $($script:Failures.Count)
- **Warnings:** $($script:Warnings.Count)
- **Proof directory:** ``$ProofDirectory``

## Checks

| Status | Check | Details |
|---|---|---|
$($checkTable -join "`n")

## Pull Request decision

$(if ($script:Failures.Count -eq 0) {
"All mandatory automated checks passed. Review the cached diff and proof files before pushing and opening the Pull Request."
}
else {
"Do not open the Pull Request yet. Resolve every FAIL result, rerun this script, and confirm the final report is READY."
})

## Troubleshooting

When a failure occurred, review ``troubleshooting-candidates.md`` and the referenced output file.
Send that evidence to ChatGPT so the incident can be created or updated in the Notion Troubleshooting Log.
"@ | Out-File -LiteralPath $summaryMarkdown -Encoding utf8

try {
    Stop-Transcript | Out-Null
}
catch {
    # No active transcript.
}

$zipPath = Join-Path (Split-Path -Parent $ProofDirectory) "pre-pr-$timestamp.zip"
try {
    Compress-Archive `
        -Path (Join-Path $ProofDirectory "*") `
        -DestinationPath $zipPath `
        -Force

    Write-Host "[PASS] Proof archive - $zipPath" -ForegroundColor Green
}
catch {
    Write-Host "[WARN] Proof archive - Could not create ZIP: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Review status: $statusWord" -ForegroundColor $(if ($statusWord -eq "READY") { "Green" } else { "Red" })
Write-Host "Failures:      $($script:Failures.Count)"
Write-Host "Warnings:      $($script:Warnings.Count)"
Write-Host "Report:        $summaryMarkdown"
Write-Host "JSON summary:  $summaryJson"
Write-Host "Proof ZIP:     $zipPath"

if ($script:Failures.Count -gt 0) {
    exit 1
}

exit 0
