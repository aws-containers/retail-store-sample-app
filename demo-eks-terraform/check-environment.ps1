Write-Host "Checking environment readiness to deploy a cluster..."

# Check if AWS CLI is installed
if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    Write-Host "aws cli is not installed. Please install it." -ForegroundColor Red
    return
}

# Check if Terraform is installed
if (-not (Get-Command terraform -ErrorAction SilentlyContinue)) {
    Write-Host "WARN: terraform is not installed. If you intend to use it, please install it first." -ForegroundColor Yellow
}

# Check if jq is installed
if (-not (Get-Command jq -ErrorAction SilentlyContinue)) {
    Write-Host "jq is not installed. Please install it." -ForegroundColor Red
    return
}

# Verify correct region
$current_region = $Env:AWS_REGION
if (-not $current_region) {
    $current_region = aws configure get region
}

if (-not $current_region) {
    $current_region = $Env:AWS_DEFAULT_REGION
}

if ($current_region -ne "us-east-1") {
    if ($current_region) {
        Write-Host "The current region is $current_region. This must be deployed in us-east-1." -ForegroundColor Red
        return
    }
    Write-Host "Unable to determine the current region. Use `aws configure` to set the default region to us-east-1." -ForegroundColor Red
    return
}

Write-Host "- Running in correct region: us-east-1" -ForegroundColor Green

# Get default VPC
$VPC_ID = aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text

if ($VPC_ID -eq "None") {
    Write-Host "Error: No default VPC found." -ForegroundColor Red
    return
}

Write-Host "- Using default VPC: $VPC_ID" -ForegroundColor Green

# Check for Internet Gateway
$IGW_ID = aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$VPC_ID" --query "InternetGateways[0].InternetGatewayId" --output text

if ($IGW_ID -eq "None") {
    Write-Host "Error: Default VPC $VPC_ID does not have an Internet Gateway attached." -ForegroundColor Red
    return
}

Write-Host "- Default VPC $VPC_ID has an Internet Gateway: $IGW_ID" -ForegroundColor Green

# Get main route table
$MAIN_ROUTE_TABLE_ID = aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$VPC_ID" "Name=association.main,Values=true" --query "RouteTables[0].RouteTableId" --output text

if ($MAIN_ROUTE_TABLE_ID -eq "None") {
    Write-Host "Error: Default VPC $VPC_ID does not have a main route table." -ForegroundColor Red
    return
}

Write-Host "- The main route table for Default VPC $VPC_ID is: $MAIN_ROUTE_TABLE_ID" -ForegroundColor Green

# Check if main route table has route to IGW
$ROUTE_TO_IGW = aws ec2 describe-route-tables --route-table-ids $MAIN_ROUTE_TABLE_ID --query "RouteTables[0].Routes[?GatewayId=='$IGW_ID'].GatewayId" --output text

if ($ROUTE_TO_IGW -eq $IGW_ID) {
    Write-Host "- The main route table $MAIN_ROUTE_TABLE_ID has a route to the Internet Gateway $IGW_ID." -ForegroundColor Green
} else {
    Write-Host "The main route table $MAIN_ROUTE_TABLE_ID does not have a route to the Internet Gateway $IGW_ID." -ForegroundColor Red
    return
}

Write-Host "Checking for required subnets and their configurations..."

# Fetch the list of availability zones
$available_zones = aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output json | ConvertFrom-Json

# Define the required suffixes
$required_suffixes = @("a", "b", "c")

# Initialize lists to track errors
$missing_subnets = @()
$map_public_ip_errors = @()
$route_table_errors = @()

# Check for subnets in each required zone
foreach ($suffix in $required_suffixes) {
    # Find zones ending with the required suffix
    $zone = $available_zones | Where-Object { $_ -like "*$suffix" }

    if (-not $zone) {
        Write-Host "Error: Availability zone with suffix '$suffix' is missing." -ForegroundColor Red
        Write-Host "Please reset the lab and try again." -ForegroundColor Red
        return
    }

    # Check for subnets in the zone within the specified VPC
    $subnet_info = aws ec2 describe-subnets --filters "Name=availability-zone,Values=$zone" "Name=vpc-id,Values=$VPC_ID" --query 'Subnets[?State==`available`]' --output json | ConvertFrom-Json

    if ($subnet_info.Count -eq 0) {
        $missing_subnets += $zone
    } else {
        foreach ($subnet in $subnet_info) {
            $subnet_id = $subnet.SubnetId
            $map_public_ip = $subnet.MapPublicIpOnLaunch

            # Verify MapPublicIpOnLaunch
            if ($map_public_ip -ne $true) {
                $map_public_ip_errors += "$subnet_id ($zone)"
            }

            # Verify Route Table Association
            $subnet_route_table_id = aws ec2 describe-route-tables --filters "Name=association.subnet-id,Values=$subnet_id" --query "RouteTables[0].RouteTableId" --output text

            if ($subnet_route_table_id -ne $MAIN_ROUTE_TABLE_ID) {
                $route_table_errors += "$subnet_id ($zone)"
            }
        }
    }
}

# Handle missing subnets
if ($missing_subnets.Count -gt 0) {
    Write-Host "Error: Missing subnets in the following availability zones: $($missing_subnets -join ', ')" -ForegroundColor Red
    return
}

# Handle subnets with incorrect MapPublicIpOnLaunch attribute
if ($map_public_ip_errors.Count -gt 0) {
    Write-Host "Error: The following subnets have MapPublicIpOnLaunch set to false: $($map_public_ip_errors -join ', ')" -ForegroundColor Red
    return
}

# Handle subnets with incorrect Route Table Associations
if ($route_table_errors.Count -gt 0) {
    Write-Host "Error: The following subnets are not associated with the main route table or are explicitly associated with a different route table: $($route_table_errors -join ', ')" -ForegroundColor Red
    return
}

# Check for eksClusterRole being present
if (aws iam get-role --role-name eksClusterRole > $null 2>&1) {
    # Role is present, suppress generation by terraform
    $env:TF_VAR_use_predefined_role = $true
}

Write-Host "Good to go!" -ForegroundColor Green
