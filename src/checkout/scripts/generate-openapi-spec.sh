#!/bin/bash

set -euo pipefail

SCRIPT_DIR=$(dirname "$0")

curl -s localhost:8080/v2/api-docs | yq r --prettyPrint - > $SCRIPT_DIR/../openapi.yml