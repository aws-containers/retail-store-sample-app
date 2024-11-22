#!/usr/bin/env bash

set -Eeuo pipefail

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

CHARTS_DIR="$script_dir/../deploy/kubernetes/charts"

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
  repository='public.ecr.aws/aws-containers'
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

    local chart_name=$(yq '.name' $chart_dir/Chart.yaml)
    local chart_version=$(yq '.version' $chart_dir/Chart.yaml)
    
    echo "Publishing chart: $chart_name"

    helm dependency build $chart_dir
    
    # Package the Helm chart
    helm package "$chart_dir" --destination /tmp/
        
    # Push the Helm chart to ECR
    helm push /tmp/${chart_name}-${chart_version}.tgz "oci://${repository}/"
    
    # Clean up the packaged chart file
    rm /tmp/${chart_name}-${chart_version}.tgz
}

# Main script execution
main() {
  aws ecr-public get-login-password --region us-east-1 | helm registry login --username AWS --password-stdin "${repository}"

  # Recursively find and process Helm charts
    find "$CHARTS_DIR" -name Chart.yaml -not -path "*/opentelemetry/*" -exec dirname {} \; | sort -z | while read -r chart_dir; do
        publish_chart "$chart_dir" 
    done
}

# Run the main function
main

echo "All charts have been published to $repository"
