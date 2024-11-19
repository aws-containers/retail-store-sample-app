#!/usr/bin/env bash

set -Eeuo pipefail

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)
SOURCE_DIR="$script_dir/../deploy/kubernetes/charts"
REGION="us-east-1"
REPO_URL="public.ecr.aws/b3u2a5x0"

usage() {
  cat <<EOF
Usage: $(basename "${BASH_SOURCE[0]}") [-h] [-a] [-s service] [-r repository] [-t tag]

Push helm charts to ECR for one or more services.

Available options:

-h, --help       Print this help and exit
-r, --repository Repository name to push the charts to
EOF
  exit
}

parse_params() {
  # default values of variables set from params
  repository=''
  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -r | --repository)
      repository="${2-}"
      shift
      ;;
    -?*) die "Unknown option: $1" ;;
    *) break ;;
    esac
    shift
  done

  args=("$@")

  # check required params and arguments
  return 0
}

parse_params "$@"

# Function to publish Helm chart to ECR
publish_chart() {
    local chart_dir=$1
    local chart_name=$(basename "$chart_dir")
   # local helm_repository=$2
    
    echo "Publishing chart: $chart_name"
    
    # Package the Helm chart
    helm package "$chart_dir" --destination /tmp/
    #echo $helm_repository
        
    # Push the Helm chart to ECR
    helm push /tmp/"${chart_name}-"*.tgz "oci://${ECR_REPO_NAME}/"
    
    # Clean up the packaged chart file
    rm /tmp/"${chart_name}-"*.tgz
}

# Main script execution
main() {
    # Ensure AWS CLI is configured for ECR public
  #  aws ecr-public get-login-password --region us-east-1 | helm registry login --username AWS --password-stdin "${ECR_REPO_NAME}"
   ECR_REPO_NAME="$REPO_URL/$repository"
   aws ecr-public get-login-password --region us-east-1 | helm registry login --username AWS --password-stdin "${ECR_REPO_NAME}"
  # Recursively find and process Helm charts
    find "$SOURCE_DIR" -name Chart.yaml -exec dirname {} \; | sort -z | while read -r chart_dir; do
        publish_chart "$chart_dir" 
    done
}

# Run the main function
main

echo "All charts have been published to $REPO_URL"
