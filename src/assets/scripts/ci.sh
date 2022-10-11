#!/bin/bash

set -euo pipefail

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

cd $DIR/..

docker build -t assets-ci .

docker run -d --rm -p 8080:8080 -e PORT=8080 assets-ci

echo "Waiting for container..."

sleep 5

# Test
response=$(curl --write-out '%{http_code}' --silent --output /dev/null localhost:8080/health.html)

if [[ "$response" -ne 200 ]] ; then
  echo "Error: Received status code $response"
  exit 1
fi