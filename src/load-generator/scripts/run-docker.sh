#!/usr/bin/env bash

set -Eeuo pipefail
trap cleanup SIGINT SIGTERM ERR EXIT

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

usage() {
  cat <<EOF
Usage: $(basename "${BASH_SOURCE[0]}") [-h] [-v] [-q] [-t target] [--vu users] [-d duration] [-c]

Builds container images for one or more services.

Available options:

-h, --help       Print this help and exit
-v, --verbose    Print script debug info
-q, --quiet      Reduce log output
-t, --target     Target URL to use for the test  (default: http://localhost:8888)
--vu             Number of virtual users to run  (default: 1)
-d, --duration   Duration of the test in seconds (default: forever)
-n, --network   Docker network to use            (default: bridge)

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
  quiet=false
  container=false
  target='http://localhost:8888'
  duration='0'
  vus='1'
  network='bridge'
  output=''

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -q | --quiet) quiet=true ;;
    -n | --network)
      network="${2-}"
      shift
      ;;
    -t | --target)
      target="${2-}"
      shift
      ;;
    -d | --duration)
      duration="${2-}"
      shift
      ;;
    -o | --output)
      output="${2-}"
      shift
      ;;
    --vu)
      vus="${2-}"
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

cd $script_dir/../

quiet_args=''

if [ "$quiet" = true ]; then
  quiet_args='-q'
fi

overrides_args="{\"config\": { \"phases\": [{ \"duration\": $duration, \"arrivalRate\": $vus }] } }"

docker build -t retail-store-sample-loadgen:run --pull --quiet -f Dockerfile.run .

container_name="retail-store-loadgen-$(date +%s)"

docker run --name "$container_name" --network $network -v $PWD:/scripts \
  retail-store-sample-loadgen:run \
  run -t $target $quiet_args --output /tmp/output.json --overrides "$overrides_args" /scripts/scenario.yml

if [ -n "$output" ]; then
  docker cp "$container_name:/tmp/output.json" "$output"
fi

docker rm "$container_name"