#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

(cd $DIR/../deploy/docker-compose && docker-compose build)

(cd $DIR/../deploy/docker-compose && docker-compose up -d)

sleep 30

(cd $DIR/../src/e2e && ./scripts/run-docker.sh -n docker-compose_default 'http://ui:8080')

(cd $DIR/../deploy/docker-compose && docker-compose down)