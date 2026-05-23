# This script needs to be sourced

RED="\e[0;31m"
GREEN="\e[0;32m"
YELLOW="\e[0;33m"
MAGENTA="\e[0;35m"
NC="\e[0m"

echo "Checking environment readiness to deploy a cluster..."

if [[ -d "/Applications" ]] && [[ -d "/Library" ]] ; then
    echo -e "- ${MAGENTA}Detected MacOS terminal${NC}"
elif  [ "$AWS_EXECUTION_ENV" = "CloudShell" ] ; then
    echo -e "- ${MAGENTA}Detected AWS CloudShell terminal${NC}"
elif [ "$(netstat -ptn 2>&1 | grep '^tcp.*ttyd' | awk '{ split($4, a, ":"); split($7, b, "/"); printf "%s:%s\n", a[2], b[2] }')" = "8080:ttyd" ] ; then
    echo -e "- ${MAGENTA}Detected KodeKloud lab terminal${NC}"
else
    {
    source /etc/os-release
    echo -e "- ${MAGENTA}Detected Linux terminal: ${NAME}${NC}"
    }
fi

if ! command -v aws > /dev/null ; then
    echo -e "${RED}aws cli is not installed. Please install it.${NC}"
    return
fi

if ! command -v terraform > /dev/null ; then
    echo -e "${YELLOW}WARN: terraform is not installed. If you intend to use it, please install it first.${NC}"
fi

if ! command -v jq > /dev/null ; then
    echo -e "${RED}jq is not installed. Please install it.${NC}"
    return
fi

# Verify correct region
current_region=$AWS_REGION

if [[ -z "$current_region" ]]; then
    current_region=$(aws configure get region)
fi

# Fallback to environment variables if the region is not set
if [[ -z "$current_region" ]]; then
    current_region=$AWS_DEFAULT_REGION
fi

if [[ "$current_region" != "us-east-1" ]]; then
    if [[ -n "$current_region" ]]; then
        echo -e "${RED}The current region is ${current_region}. This must be deployed in us-east-1.${NC}"
        return
    fi

    echo "${RED}Unable to determine the current region. Use "aws configure" to set the default region to us-east-1.${NC}"
    return
fi

echo -e "- ${GREEN}Running in correct region: us-east-1${NC}"

VPC_ID=$(aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text)

if [ "$VPC_ID" == "None" ]; then
  echo "${RED}Error: No default VPC found.${NC}"
  return
fi

echo -e "${GREEN}- Using default VPC: ${VPC_ID}${NC}"

# Check if the Default VPC Has an Internet Gateway
IGW_ID=$(aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$VPC_ID" --query "InternetGateways[0].InternetGatewayId" --output text)

if [ "$IGW_ID" == "None" ]; then
    echo -e "${RED}Error: Default VPC $VPC_ID does not have an Internet Gateway attached.${NC}"
    return
fi

echo -e "${GREEN}- Default VPC $VPC_ID has an Internet Gateway: ${IGW_ID}${NC}"

# Step 3: Get the Main Route Table for the Default VPC
MAIN_ROUTE_TABLE_ID=$(aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$VPC_ID" "Name=association.main,Values=true" --query "RouteTables[0].RouteTableId" --output text)

if [ "$MAIN_ROUTE_TABLE_ID" == "None" ]; then
    echo -e "${RED}Error: Default VPC $VPC_ID does not have a main route table.${NC}"
    return
fi

echo -e "${GREEN}- The main route table for Default VPC $VPC_ID is: ${MAIN_ROUTE_TABLE_ID}${NC}"

# Step 4: Check if the Main Route Table Has a Route to the Internet Gateway
ROUTE_TO_IGW=$(aws ec2 describe-route-tables --route-table-ids $MAIN_ROUTE_TABLE_ID --query "RouteTables[0].Routes[?GatewayId=='$IGW_ID'].GatewayId" --output text)

if [ "$ROUTE_TO_IGW" == "$IGW_ID" ]; then
    echo -e "${GREEN}- The main route table $MAIN_ROUTE_TABLE_ID has a route to the Internet Gateway $IGW_ID.${NC}"
else
    echo -e "${RED}The main route table $MAIN_ROUTE_TABLE_ID does not have a route to the Internet Gateway $IGW_ID.${NC}"
    return
fi

echo "- Checking for required subnets and that they are porperly configured..."

# Fetch the list of availability zones
available_zones=$(aws ec2 describe-availability-zones --query 'AvailabilityZones[].ZoneName' --output json)

# Define the required suffixes
required_suffixes=("a" "b" "c")

# Initialize a list to track missing subnets or misconfigured attributes
# Initialize lists to track errors
missing_subnets=()
map_public_ip_errors=()
route_table_errors=()

# Check for a subnet in each required zone
for suffix in "${required_suffixes[@]}"; do
    # Find zones ending with the required suffix
    zone=$(echo "$available_zones" | jq -r ".[] | select(endswith(\"$suffix\"))")

    if [ -z "$zone" ]; then
        echo -e "${RED}Error: Availability zone with suffix '$suffix' is missing.${NC}"
        echo "Please reset the lab and try again."
        exit 1
    fi

    # Check for subnets in the zone within the specified VPC
    subnet_info=$(aws ec2 describe-subnets --filters "Name=availability-zone,Values=$zone" "Name=vpc-id,Values=$VPC_ID" \
        --query 'Subnets[?State==`available`]' --output json)

    subnet_count=$(echo "$subnet_info" | jq 'length')

    if [ "$subnet_count" -eq 0 ]; then
        missing_subnets+=("$zone")
    else
        # Store the subnets in a variable for iteration
        subnets=$(echo "$subnet_info" | jq -c '.[]')

        while read -r subnet; do
            subnet_id=$(echo "$subnet" | jq -r '.SubnetId')
            map_public_ip=$(echo "$subnet" | jq -r '.MapPublicIpOnLaunch')

            # Verify MapPublicIpOnLaunch
            if [ "$map_public_ip" != "true" ]; then
                map_public_ip_errors+=("$subnet_id ($zone)")
            fi

            # Verify Route Table Association
            subnet_route_table_id=$(aws ec2 describe-route-tables --filters "Name=association.subnet-id,Values=$subnet_id" \
                --query "RouteTables[0].RouteTableId" --output text)

            if [ "$subnet_route_table_id" == "None" ]; then
                subnet_route_table_id=""
            fi

            if [ -n "$subnet_route_table_id" ] && [ "$subnet_route_table_id" != "$MAIN_ROUTE_TABLE_ID" ]; then
                route_table_errors+=("$subnet_id ($zone)")
            fi
        done <<< "$subnets"
    fi
done

err=0
# Handle missing subnets
if [ ${#missing_subnets[@]} -ne 0 ]; then
    echo -e "${RED}Error: Missing subnets in the following availability zones: ${missing_subnets[*]}${NC}"
    err=1
fi

# Handle subnets with incorrect MapPublicIpOnLaunch attribute
if [ ${#map_public_ip_errors[@]} -ne 0 ]; then
    echo -e "${RED}Error: The following subnets have MapPublicIpOnLaunch set to false: ${map_public_ip_errors[*]}${NC}"
    err=1
fi

# Handle subnets with incorrect Route Table Associations
if [ ${#route_table_errors[@]} -ne 0 ]; then
    echo -e "${RED}Error: The following subnets are not associated with the main route table or are explicitly associated with a different route table: ${route_table_errors[*]}${NC}"
    err=1
fi

if [ $err -eq 1 ] ; then
    echo "Please reset the lab and try again."
    return
fi
# We have the zones.
# Check for eksClusterRole being present and flag terraform accordingly.
if aws iam get-role --role-name eksClusterRole > /dev/null 2>&1 ; then
    # Role is present, suppress generation by terraform
    export TF_VAR_use_predefined_role=true
fi

echo -e "${GREEN}Good to go!${NC}"
