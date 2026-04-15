#!/bin/bash

set -euo pipefail

manifest_file="${1:-}"
release_id="${2:-}"
git_sha="${3:-}"
created_at="${4:-}"
image_tag="${5:-}"
ecr_account_id="${6:-}"
regions_json="${7:-}"

if [ -z "${manifest_file}" ] || [ -z "${release_id}" ] || [ -z "${git_sha}" ] || [ -z "${created_at}" ] || [ -z "${image_tag}" ] || [ -z "${ecr_account_id}" ] || [ -z "${regions_json}" ]; then
  echo "Usage: $0 <manifest_file> <release_id> <git_sha> <created_at> <image_tag> <ecr_account_id> <regions_json>"
  exit 1
fi

if ! echo "${regions_json}" | jq -e 'type == "array" and length > 0' >/dev/null 2>&1; then
  echo "regions_json must be a non-empty JSON array"
  exit 1
fi

services=(catalog cart orders checkout ui)

mkdir -p "$(dirname "${manifest_file}")"

cat > "${manifest_file}" <<EOF
release:
  id: ${release_id}
  git_sha: ${git_sha}
  created_at: ${created_at}
  regions:
EOF

while IFS= read -r region; do
  cat >> "${manifest_file}" <<EOF
    - ${region}
EOF
done < <(echo "${regions_json}" | jq -r '.[]')

cat >> "${manifest_file}" <<EOF
services:
EOF

for service in "${services[@]}"; do
  cat >> "${manifest_file}" <<EOF
  ${service}:
    regions:
EOF

  while IFS= read -r region; do
    repository_name="retail-store-sample-${service}"
    repository_uri="${ecr_account_id}.dkr.ecr.${region}.amazonaws.com/${repository_name}"

    digest=$(aws ecr describe-images \
      --region "${region}" \
      --repository-name "${repository_name}" \
      --image-ids imageTag="${image_tag}" \
      --query 'imageDetails[0].imageDigest' \
      --output text)

    if [ -z "${digest}" ] || [ "${digest}" = "None" ]; then
      echo "Could not resolve digest for ${repository_uri}:${image_tag}"
      exit 1
    fi

    cat >> "${manifest_file}" <<EOF
      ${region}:
        repository: ${repository_uri}
        tag: ${image_tag}
        digest: ${digest}
EOF
  done < <(echo "${regions_json}" | jq -r '.[]')
done

echo "Release manifest created at ${manifest_file}"
