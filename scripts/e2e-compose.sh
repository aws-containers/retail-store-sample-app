#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if [ -z "$COMPOSE_NETWORK" ]; then
  COMPOSE_NETWORK="docker-compose_default"
fi

cd "$DIR/../src/e2e" && bash ./scripts/run-docker.sh -n "$COMPOSE_NETWORK" 'http://ui:8080'