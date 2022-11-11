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
-q, --quiet      Reduce log output
-t, --target     Target URL to use for the test (default: http://localhost:8888)
--vu             Number of virtual users to run (default: 1)
-d, --duration   Duration of the test in seconds (default: forever)
-c, --container  Run the load test in a container (default: directly invokes npm)

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
  target='localhost:8888'
  duration='0'
  vus='1'

  while :; do
    case "${1-}" in
    -h | --help) usage ;;
    -v | --verbose) set -x ;;
    --no-color) NO_COLOR=1 ;;
    -q | --quiet) quiet=true ;;
    -c | --container) container=true ;;
    -t | --target)
      target="${2-}"
      shift
      ;;
    -d | --duration)
      duration="${2-}"
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

msg "Performing pre-check on ${BLUE}$target/home${NOFORMAT}..."

status_code=$(curl --write-out '%{http_code}' --silent --output /dev/null $target/home)

if [[ "$status_code" -ne 200 ]] ; then
  msg "${RED}Error:${NOFORMAT} Target $target returned HTTP code $status_code, are you sure its up?"
  exit 1
fi

msg "Pre-check ${GREEN}Passed!${NOFORMAT}\n"

quiet_args=''

if [ "$quiet" = true ]; then
  quiet_args='-q'
fi

overrides_args="{\"config\": { \"phases\": [{ \"duration\": $duration, \"arrivalRate\": $vus }] } }"

if [ "$container" = true ]; then
  msg "Running in ${BLUE}container mode${NOFORMAT}..."

  docker run --rm -v $script_dir/../:/scripts \
    artilleryio/artillery:2.0.0-23 \
    run -t $target $quiet_args --overrides "$overrides_args" /scripts/scenario.yml 
else
  msg "Running in ${BLUE}default mode${NOFORMAT}..."

  npm run generator -- -t $target $quiet_args --overrides "$overrides_args"
fi

msg 'Done!'