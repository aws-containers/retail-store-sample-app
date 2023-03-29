#!/bin/bash

endpoint=$1

if [ -z "$endpoint" ]; then
  echo "Error: First argument must be endpoint (example: http://myendpoint:3000)"
  exit 1
fi

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd $DIR/../src/e2e && bash ./scripts/run-docker.sh $endpoint