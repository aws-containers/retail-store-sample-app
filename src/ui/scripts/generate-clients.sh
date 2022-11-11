#!/bin/bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

#$DIR/generate-client.sh catalog $DIR/../../catalog/openapi.yml
#$DIR/generate-client.sh carts $DIR/../../carts/openapi.yml
$DIR/generate-client.sh orders $DIR/../../orders/openapi.yml
$DIR/generate-client.sh checkout $DIR/../../checkout/openapi.yml