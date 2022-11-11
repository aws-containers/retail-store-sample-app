#!/usr/bin/env bash

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
  src_dir="$script_dir/../src"

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -q | --quiet) quiet=true ;;
    -a | --all) all=true ;;
    --src)
      src_dir="${2-}"
      shift
      ;;
    --skip-download) download=false ;;
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

output_dir="$script_dir/oss-output"

function golicenses()
{
  component=$1

  component_dir="$src_dir/$component"

  component_output_dir="$output_dir/$component"

  mkdir -p $component_output_dir

  (cd $component_dir && go-licenses report --template $script_dir/golicenses/template.tpl --ignore github.com/aws-containers/retail-store-sample-app . > $component_output_dir/licenses.csv)
}

function run_ort()
{
  component=$1
  profile=$2

  component_dir="$src_dir/$component"

  component_output_dir="$output_dir/$component"

  rm -rf $component_output_dir

  ort analyze \
    -i $component_dir \
    --repository-configuration-file "$script_dir/ort/$profile.ort.yml" \
    --package-curations-file $script_dir/ort/curations.yml \
    -o $component_output_dir -f JSON

  ort report \
    -i $component_output_dir/analyzer-result.json \
    -o $component_output_dir \
    --report-formats NoticeTemplate,StaticHtml

  if [ "$download" = true ] ; then
    ort download \
      -i $component_output_dir/analyzer-result.json \
       --package-types PACKAGE \
      -o $component_output_dir/src
  fi
  
}

if [ -f "$output_dir" ]; then
  echo "Error: Output directory already exists please remove it"
  exit 1
fi

function do_component()
{
  component=$1

  component_dir="$script_dir/../src/$component"

  component_output_dir="$output_dir/$component"

  msg "Processing component ${GREEN}$component${NOFORMAT}..."

  if [ ! -d "$component_dir" ]; then
    echo "Error: Component $component does not exist"
    exit 1
  fi

  source $component_dir/scripts/oss.source

  (cd $script_dir/attribution && python3 main.py $component $component_output_dir $component_dir/ATTRIBUTION.md)

  rm -rf $component_output_dir/src
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