#!/bin/bash

set -euo pipefail

SCRIPT_DIR=$(dirname "$0")

export AWS_ACCESS_KEY_ID="dummy"
export AWS_SECRET_ACCESS_KEY="dummy"

(cd $SCRIPT_DIR/.. && ./mvnw verify)

yq $SCRIPT_DIR/../target/openapi.json -P > $SCRIPT_DIR/../openapi.yml