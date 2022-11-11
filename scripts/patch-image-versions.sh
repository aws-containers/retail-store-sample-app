#!/bin/bash

set -euo pipefail

if [ -z "$TAG" ]; then
  echo "Error: TAG must be set"
  exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

for i in $DIR/../src/**/chart/values.yaml; do
    # Could use yq here but it strips whitespace
    # https://github.com/mikefarah/yq/issues/465
    sed -ri "0,/tag/{s/^(\s*)(tag:.*$)/\1tag: \"$TAG\"/}" $i
done

cat << EOF > $DIR/../deploy/terraform/modules/image-tag/outputs.tf
output "image_tag" {
  value = "$TAG"
}
EOF

echo "IMAGE_TAG='$TAG'" > $DIR/image-tag.sh