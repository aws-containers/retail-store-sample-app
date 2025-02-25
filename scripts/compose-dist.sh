#!/bin/bash

set -e

if [ -z "$IMAGE_TAG" ]; then
  echo "Error: Set IMAGE_TAG environment variable"
  exit 1
fi

outfile=$(mktemp)

docker compose --project-directory src/app --project-name retail-sample config --no-interpolate > "$outfile"

yq eval -i '(.services[] | select(has("build"))) |= . + {"image": "public.ecr.aws/aws-containers/retail-store-sample-\(key):\(env(IMAGE_TAG))"}' "$outfile"

yq eval -i 'del(.. | select(has("build")).build)' "$outfile"
yq eval -i 'del(.. | select(has("develop")).develop)' "$outfile"

mkdir -p dist/docker-compose

cp "$outfile" dist/docker-compose/docker-compose.yaml

rm "$outfile"