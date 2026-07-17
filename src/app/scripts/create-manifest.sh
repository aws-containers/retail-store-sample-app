#!/usr/bin/env bash

set -euo pipefail

REPOSITORY="${1:?repository is required}"
TAG="${2:?tag is required}"
TARGET_MANIFEST="${REPOSITORY}:${TAG}"

# Replace the manifest if it already exists locally from a previous run.
docker manifest rm "${TARGET_MANIFEST}" >/dev/null 2>&1 || true

docker manifest create "${TARGET_MANIFEST}" \
  "${REPOSITORY}:${TAG}-amd64" \
  "${REPOSITORY}:${TAG}-arm64"

docker manifest annotate "${TARGET_MANIFEST}" "${REPOSITORY}:${TAG}-amd64" --arch amd64
docker manifest annotate "${TARGET_MANIFEST}" "${REPOSITORY}:${TAG}-arm64" --arch arm64

docker manifest push "${TARGET_MANIFEST}"
