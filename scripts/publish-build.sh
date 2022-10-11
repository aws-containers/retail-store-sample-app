#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

timestamp=$(date +%s)
tag="build.$timestamp"

git tag -a $tag -m "Published build $tag"
git push origin $tag