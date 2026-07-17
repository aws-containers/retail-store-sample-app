#!/usr/bin/bash

set -e pipefail
VERSION=${1:-"latest"}
# Build the cart image 
cd ../../cart
docker build -t cart:${VERSION} .

cd ../catalog
docker build -t catalog:${VERSION} .

cd ../checkout
docker build -t checkout:${VERSION} .

cd ../orders
docker build -t orders:${VERSION} .
