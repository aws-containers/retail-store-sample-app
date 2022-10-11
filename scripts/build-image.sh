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
  arm=false
  quiet=false
  builder='nil'
  repository='aws-containers'
  tag='latest'
  service='*'

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -p | --push) push=true ;;
    -q | --quiet) quiet=true ;;
    -a | --all) all=true ;;
    --cnb) cnb=true ;;
    --arm) arm=true ;;
    -s | --service)
      service="${2-}"
      shift
      ;;
    -r | --repository)
      repository="${2-}"
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

  if [ -f "$component_dir/scripts/build.source" ]; then
    source "$component_dir/scripts/build.source"
  fi

  if [ "$cnb" = true ] || [ "$all" = true ]; then
    if [ "$all" = true ]; then
      cnb_tag="$tag-cnb"
    else
      cnb_tag="$tag"
    fi

    msg "Running pack build..."
    pack $quiet_args --no-color build $image_name:build --builder $builder --path $component_dir --tag $repository/$image_name:$cnb_tag $pack_args

    if [ "$push" = true ] ; then
      msg "Pushing image for ${GREEN}$component${NOFORMAT}..."

      docker push -q $repository/$image_name:$cnb_tag
    fi
  fi

  if [ "$cnb" != true ] || [ "$all" = true ]; then
    if [ "$arm" = true ] || [ "$all" = true ]; then
      push_args=""

      if [ "$push" = true ] ; then
        push_args="--push"
      fi

      msg "Running Docker buildx..."
      docker buildx build --progress plain $push_args --platform linux/amd64,linux/arm64 $quiet_args -f "$component_dir/$dockerfile" $docker_build_args -t $repository/$image_name:$tag $component_dir
    else
      msg "Running Docker build..."
      docker build $quiet_args -f "$component_dir/$dockerfile" $docker_build_args -t $repository/$image_name:$tag $component_dir

      if [ "$push" = true ] ; then
        msg "Pushing image for ${GREEN}$component${NOFORMAT}..."

        docker push -q $repository/$component:$tag
      fi
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
else
  build "$service"
fi

msg "\nBuild complete!"