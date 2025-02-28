#!/bin/bash

set -e

if [ -z "$IMAGE_TAG" ]; then
  echo "Error: Set IMAGE_TAG environment variable"
  exit 1
fi

outfile=$(mktemp)

LOAD_BALANCER=yes helmfile template -f src/app/helmfile.yaml --skip-tests > "$outfile"

mkdir -p dist/kubernetes

cp "$outfile" dist/kubernetes/kubernetes.yaml

rm "$outfile"