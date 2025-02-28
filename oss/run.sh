#!/usr/bin/env bash

ORT_VERSION="52.0.1"

set -Eeuo pipefail
trap cleanup SIGINT SIGTERM ERR EXIT

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

usage() {
  cat <<EOF
Usage: $(basename "${BASH_SOURCE[0]}") [-h] [-v] [-q] [-p] [-a] [--cnb] [-s service] [-r repository] [-t tag]

Builds container images for one or more services.

Available options:

-h, --help       Print this help and exit
-v, --verbose    Print script debug info
-q, --quiet      Reduce log output for docker/pack
-s, --service    Name of the environment to build images for, '*' for all services (default)
-r, --repository Repository name used to tag the image(s)
-t, --tag        Tag for the image(s)
-p, --push       Push the image(s) to the specified repository
--cnb            Uses Cloud Native Buildpacks to build the image(s)
-a, --all        Builds both Docker and Cloud Native Buildpacks image(s)
EOF
  exit
}

cleanup() {
  trap - SIGINT SIGTERM ERR EXIT

  # script cleanup here
}

setup_colors() {
  if [[ -t 2 ]] && [[ -z "${NO_COLOR-}" ]] && [[ "${TERM-}" != "dumb" ]]; then
    NOFORMAT='\033[0m' RED='\033[0;31m' GREEN='\033[0;32m' ORANGE='\033[0;33m' BLUE='\033[0;34m' PURPLE='\033[0;35m' CYAN='\033[0;36m' YELLOW='\033[1;33m'
  else
    NOFORMAT='' RED='' GREEN='' ORANGE='' BLUE='' PURPLE='' CYAN='' YELLOW=''
  fi
}

msg() {
  echo >&2 -e "${1-}"
}

die() {
  local msg=$1
  local code=${2-1} # default exit status 1
  msg "$msg"
  exit "$code"
}

parse_params() {
  # default values of variables set from params
  download=true
  service='*'
  root=false

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -q | --quiet) quiet=true ;;
    -a | --all) all=true ;;
    --skip-download) download=false ;;
    --root) root=true ;;
    -s | --service)
      service="${2-}"
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
setup_colors

temp_dir=$(mktemp -d)

echo "Using temp dir $temp_dir"

base_dir="/project"
src_dir="$base_dir/src"
ort_dir="$base_dir/oss/ort"

root_args=""

if [ "$root" = true ] ; then
  root_args="--user 0"
fi

function run_ort()
{
  component=$1
  profile=$2
  output_dir=$3

  container_component_dir="$src_dir/$component"

  docker run --rm $root_args -v $output_dir:/output -v $script_dir/..:/project ghcr.io/oss-review-toolkit/ort-minimal:$ORT_VERSION analyze \
    -i $container_component_dir -o /output \
    --repository-configuration-file "$ort_dir/$profile.ort.yml" -f JSON

  docker run --rm $root_args -v $output_dir:/output -v $script_dir/..:/project ghcr.io/oss-review-toolkit/ort-minimal:$ORT_VERSION download \
    -i /output/analyzer-result.json -o /output/src
}

function do_component()
{
  component=$1

  component_dir="$script_dir/../src/$component"

  component_output_dir="$temp_dir/$component"

  mkdir -p $component_output_dir

  msg "Processing component ${GREEN}$component${NOFORMAT}..."

  if [ ! -d "$component_dir" ]; then
    echo "Error: Component $component does not exist"
    exit 1
  fi

  if [ -f "$component_dir/pom.xml" ]; then
    echo "Detected Java"
    run_ort $component 'maven' $component_output_dir
  elif [ -f "$component_dir/go.mod" ]; then
    echo "Detected Go"
    run_ort $component 'go-mod' $component_output_dir
  elif [ -f "$component_dir/package.json" ]; then
    echo "Detected Javascript"
    run_ort $component 'npm' $component_output_dir
  else
    echo "Error: Component $component does not contain a supported build system"
    exit 1
  fi

  (cd $script_dir/attribution && yarn start $component_output_dir $component_dir/ATTRIBUTION.md)

  echo "Cleaning up..."

  #rm -rf $component_output_dir/src
}

if [[ "$service" = '*' ]]; then
  do_component 'ui'
  do_component 'catalog'
  do_component 'cart'
  do_component 'checkout'
  do_component 'orders'
else
  do_component "$service"
fi

msg "\nBuild complete!"