#!/bin/bash

set -e

client=$1
endpoint=$2

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

outdir=$(mktemp -d)

openapi-generator generate \
  -i $endpoint \
  --api-package com.amazon.sample.ui.clients.$client.api \
  --model-package com.amazon.sample.ui.clients.$client.model \
  -g java \
  --library webclient \
  --skip-validate-spec \
  -o $outdir

if [ -d $DIR/../src/main/java/com/amazon/sample/ui/clients/$client ]; then
  rm -rf $DIR/../src/main/java/com/amazon/sample/ui/clients/$client
fi

(cd $outdir/src/main/java && find . -name '*.java' | cpio -pdm $DIR/../src/main/java)