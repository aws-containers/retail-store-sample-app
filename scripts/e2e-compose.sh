#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd $DIR/../src/e2e && bash ./scripts/run-docker.sh -n docker-compose_default 'http://ui:8080'