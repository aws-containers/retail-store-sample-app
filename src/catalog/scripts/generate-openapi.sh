#!/bin/bash

set -e

if [ ! -f "main.go" ]; then
  echo "Error: Must execute this script from directory with main.go"
  exit 1
fi

swag init