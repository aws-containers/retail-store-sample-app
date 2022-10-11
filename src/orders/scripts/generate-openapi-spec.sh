#!/bin/bash

set -euo pipefail

SCRIPT_DIR=$(dirname "$0")

(cd $SCRIPT_DIR/.. && ./mvnw verify)

yq $SCRIPT_DIR/../target/openapi.json -P > $SCRIPT_DIR/../openapi.yml