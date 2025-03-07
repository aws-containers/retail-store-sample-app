#!/bin/bash

repository=$1
tag=$2

if [ -z "$repository" ]; then
  echo "Error: First argument must be repository"
  exit 1
fi

if [ -z "$tag" ]; then
  echo "Error: Second argument must be tag"
  exit 1
fi

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

amd64_manifest=$(docker manifest inspect $repository:$tag-amd64)
amd64_digest=$(echo $amd64_manifest | jq -r '.manifests[] | select(.platform.architecture == "amd64") | .digest')

arm64_manifest=$(docker manifest inspect $repository:$tag-arm64)
arm64_digest=$(echo $arm64_manifest | jq -r '.manifests[] | select(.platform.architecture == "arm64") | .digest')

target_manifest="$repository:$tag"

docker manifest create $target_manifest $repository@${amd64_digest} $repository@${arm64_digest}

docker manifest push $target_manifest