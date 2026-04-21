#!/usr/bin/env bash

set -euo pipefail

MANIFEST_FILE="${1:-}"
RELEASE_ID="${2:-}"
GIT_SHA="${3:-}"
CREATED_AT="${4:-}"
IMAGE_TAG="${5:-}"
ECR_REGISTRIES_JSON="${6:-}"
AWS_REGIONS_JSON="${7:-}"

if [ -z "${MANIFEST_FILE}" ] || [ -z "${RELEASE_ID}" ] || [ -z "${GIT_SHA}" ] || [ -z "${CREATED_AT}" ] || [ -z "${IMAGE_TAG}" ] || [ -z "${ECR_REGISTRIES_JSON}" ] || [ -z "${AWS_REGIONS_JSON}" ]; then
  echo "Usage: generate-release-manifest.sh <manifest_file> <release_id> <git_sha> <created_at> <image_tag> <ecr_registries_json> <aws_regions_json>"
  exit 1
fi

if ! echo "${AWS_REGIONS_JSON}" | jq -e 'type == "array" and length > 0 and all(.[]; type == "string" and length > 0)' >/dev/null 2>&1; then
  echo "AWS_REGIONS_JSON must be a non-empty JSON array"
  exit 1
fi

if ! echo "${ECR_REGISTRIES_JSON}" | jq -e 'type == "object"' >/dev/null 2>&1; then
  echo "ECR_REGISTRIES_JSON must be a JSON object"
  exit 1
fi

services=(catalog cart orders checkout ui)

mkdir -p "$(dirname "${MANIFEST_FILE}")"

{
  echo "apiVersion: retailstore.aws/v1alpha1"
  echo "kind: ReleaseManifest"
  echo "metadata:"
  echo "  release_id: ${RELEASE_ID}"
  echo "  created_at: ${CREATED_AT}"
  echo "  git_sha: ${GIT_SHA}"
  echo "spec:"
  echo "  image_tag: ${IMAGE_TAG}"
  echo "  regions:"
  for region in $(echo "${AWS_REGIONS_JSON}" | jq -r '.[]'); do
    echo "    - ${region}"
  done
  echo "  services:"

  for service in "${services[@]}"; do
    repository_name="retail-store-sample-${service}"
    echo "    - name: ${service}"
    echo "      images:"

    for region in $(echo "${AWS_REGIONS_JSON}" | jq -r '.[]'); do
      registry="$(echo "${ECR_REGISTRIES_JSON}" | jq -r --arg r "${region}" '.[$r] // empty')"
      if [ -z "${registry}" ]; then
        echo "Missing registry for region '${region}' in ECR_REGISTRIES_JSON"
        exit 1
      fi

      digest="$(aws ecr describe-images \
        --region "${region}" \
        --repository-name "${repository_name}" \
        --image-ids imageTag="${IMAGE_TAG}" \
        --query 'imageDetails[0].imageDigest' \
        --output text)"

      if [ -z "${digest}" ] || [ "${digest}" = "None" ]; then
        echo "Missing digest for ${repository_name}:${IMAGE_TAG} in region ${region}"
        exit 1
      fi

      echo "        - region: ${region}"
      echo "          image: ${registry}/${repository_name}:${IMAGE_TAG}"
      echo "          digest: ${digest}"
    done
  done
} > "${MANIFEST_FILE}"

echo "Generated ${MANIFEST_FILE}"
