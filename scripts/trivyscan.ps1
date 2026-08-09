$SafeName = $TargetImage -replace '[:/\\]', '_'
$ReportPath = Join-Path $ProofDir "$SafeName-trivy.json"
$LogPath = Join-Path $ProofDir "$SafeName-trivy.log"

docker image inspect $TargetImage | Out-Null

if ($LASTEXITCODE -ne 0) {
    throw "Cannot scan a missing image: $TargetImage"
}

$Arguments = @(
    "run"
    "--rm"
    "-v"
    "/var/run/docker.sock:/var/run/docker.sock"
    "--mount"
    "type=bind,src=$TrivyCache,dst=/root/.cache"
    "--mount"
    "type=bind,src=$ProofDir,dst=/reports"
    $TrivyImage
    "image"
    "--image-src"
    "docker"
    "--timeout"
    "15m"
    "--scanners"
    "vuln"
    "--no-progress"
    "--severity"
    "HIGH,CRITICAL"
    "--ignore-unfixed"
    "--format"
    "json"
    "--output"
    "/reports/$SafeName-trivy.json"
    $TargetImage
)

$PreviousPreference = $ErrorActionPreference
$ErrorActionPreference = "Continue"

try {
    $Output = & docker @Arguments 2>&1
    $ScanExitCode = $LASTEXITCODE
}
finally {
    $ErrorActionPreference = $PreviousPreference
}

$Output |
    Out-File `
        -FilePath $LogPath `
        -Encoding utf8

if ($ScanExitCode -ne 0) {
    Get-Content $LogPath
    throw "Trivy execution failed for $TargetImage."
}

if (-not (Test-Path $ReportPath)) {
    throw "Trivy report was not created."
}

if ((Get-Item $ReportPath).Length -eq 0) {
    throw "Trivy report is empty."
}

Write-Host "PASS: Trivy scan completed."