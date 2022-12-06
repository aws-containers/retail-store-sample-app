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
  push=false
  all=false
  cnb=false
  multi_arch=false
  quiet=false
  builder='nil'
  repository='public.ecr.aws/aws-containers'
  tag='latest'
  service='*'
  expected_ref=''
  actions_cache=false

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -p | --push) push=true ;;
    -q | --quiet) quiet=true ;;
    -a | --all) all=true ;;
    --cnb) cnb=true ;;
    --multi-arch) multi_arch=true ;;
    --actions-cache) actions_cache=true ;;
    -s | --service)
      service="${2-}"
      shift
      ;;
    -r | --repository)
      repository="${2-}"
      shift
      ;;
    --expected-ref)
      expected_ref="${2-}"
      shift
      ;;
    -t | --tag)
      tag="${2-}"
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

quiet_args='-q'

if [ "$push" = true ] ; then
  quiet_args='-q'
fi

function build()
{
  component=$1

  component_dir="$script_dir/../src/$component"

  msg "Building component ${GREEN}$component${NOFORMAT}..."

  if [ ! -d "$component_dir" ]; then
    echo "Error: Component $component does not exist"
    exit 1
  fi

  dockerfile="Dockerfile"
  docker_build_args=''
  pack_args=''
  image_name="retail-store-sample-$component"
  tag_prefix=''

  if [ -f "$component_dir/scripts/build.source" ]; then
    source "$component_dir/scripts/build.source"
  fi

  tag="${tag_prefix}${tag}"

  if [ -z "$expected_ref" ]; then
    ref="$repository/$image_name:$tag"
  else
    ref="$expected_ref"
  fi

  if [ "$cnb" = true ] || [ "$all" = true ]; then
    if [ "$all" = true ]; then
      cnb_tag="$tag-cnb"
    else
      cnb_tag="$tag"
    fi

    msg "Running pack build..."
    pack $quiet_args --no-color build $image_name:build --builder $builder --path $component_dir --tag "$ref-cnb" $pack_args

    if [ "$push" = true ] ; then
      msg "Pushing image for ${GREEN}$component${NOFORMAT}..."

      docker push -q "$ref-cnb"
    fi
  fi

  if [ "$cnb" != true ] || [ "$all" = true ]; then
    push_args=""

    if [ "$push" = true ] ; then
      push_args="--push"
    elif [ "$multi_arch" != true ]; then
      push_args="--load"
    fi

    cache_args=""

    if [ "$actions_cache" = true ] ; then
      echo "Using GitHub Actions cache configuration"
      cache_args="--cache-from type=gha,scope=${component} --cache-to type=gha,scope=${component},mode=max"
    fi

    if [ "$multi_arch" = true ] || [ "$all" = true ]; then
      msg "Building multi-arch..."
      docker buildx build --progress plain $cache_args $push_args --platform linux/amd64,linux/arm64 -f "$component_dir/$dockerfile" $docker_build_args -t $ref $component_dir
    else
      msg "Building local arch..."
      docker buildx build --progress plain $cache_args $push_args -f "$component_dir/$dockerfile" $docker_build_args -t $ref $component_dir
    fi
  fi

  msg "Finished ${GREEN}$component${NOFORMAT}!"
}

if [[ "$service" = '*' ]]; then
  build 'ui'
  build 'catalog'
  build 'cart'
  build 'orders'
  build 'checkout'
  build 'assets'
  build 'load-generator'
else
  build "$service"
fi

msg "\nBuild complete!"
