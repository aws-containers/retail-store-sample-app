#!/usr/bin/env bash

set -euo pipefail

VERSION="${1:-latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$(cd "${SCRIPT_DIR}/../.." && pwd)"

services=(
  cart
  catalog
  checkout
  orders
  ui
)

for service in "${services[@]}"; do
  echo "Building ${service}:${VERSION}"
  docker build -t "${service}:${VERSION}" "${SRC_DIR}/${service}"
done
