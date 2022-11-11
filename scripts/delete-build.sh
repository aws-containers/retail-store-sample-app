#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

tag=$1

if [ -z "$tag" ]; then
  echo "Error: Must specify tag"
  exit 1
fi

git tag -d $tag
git push --delete origin $tag 