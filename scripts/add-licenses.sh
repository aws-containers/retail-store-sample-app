#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

addlicense -f $DIR/misc/license-header.txt \
  --ignore "**/*.yml" \
  --ignore "**/*.yaml" \
  --ignore "**/*.xml" \
  --ignore "**/*.html" \
  --ignore "**/*.sh" \
  --ignore "**/*.sql" \
  --ignore "**/Dockerfile" \
  $DIR/../src/ui \
  $DIR/../src/assets \
  $DIR/../src/cart \
  $DIR/../src/catalog \
  $DIR/../src/orders \
  $DIR/../src/checkout/src