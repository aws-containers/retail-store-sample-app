#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VARIANT="${1:-current}"
OUTPUT_FILE="${2:-${ROOT_DIR}/docker-build-metrics-${VARIANT}-$(date +%Y%m%d-%H%M%S).csv}"

# Optional extra docker build flags, for example:
# BUILD_FLAGS="--no-cache --pull"
BUILD_FLAGS="${BUILD_FLAGS:-}"
EXTRA_ARGS=()
if [[ -n "${BUILD_FLAGS}" ]]; then
  # shellcheck disable=SC2206
  EXTRA_ARGS=( ${BUILD_FLAGS} )
fi

services=(
  "cart|src/cart|Dockerfile"
  "catalog|src/catalog|Dockerfile"
  "checkout|src/checkout|Dockerfile"
  "orders|src/orders|Dockerfile"
  "ui|src/ui|Dockerfile"
  "e2e|src/e2e|Dockerfile.run"
  "load-generator|src/load-generator|Dockerfile"
  "load-generator-run|src/load-generator|Dockerfile.run"
)

echo "service,variant,image_tag,elapsed_seconds,image_size_bytes,image_size_mb" > "${OUTPUT_FILE}"

for item in "${services[@]}"; do
  IFS='|' read -r service context dockerfile <<< "${item}"

  image_tag="local/${service}:${VARIANT}"
  context_dir="${ROOT_DIR}/${context}"
  dockerfile_path="${context_dir}/${dockerfile}"

  if [[ ! -f "${dockerfile_path}" ]]; then
    echo "Skipping ${service}: missing ${dockerfile_path}" >&2
    continue
  fi

  echo "Building ${service} (${dockerfile}) as ${image_tag} ..."

  start_ts="$(date +%s)"
  docker build "${EXTRA_ARGS[@]}" -f "${dockerfile_path}" -t "${image_tag}" "${context_dir}" >/dev/null
  end_ts="$(date +%s)"

  elapsed="$(( end_ts - start_ts ))"
  size_bytes="$(docker image inspect "${image_tag}" --format '{{.Size}}')"
  size_mb="$(awk -v b="${size_bytes}" 'BEGIN { printf "%.2f", b/1024/1024 }')"

  echo "${service},${VARIANT},${image_tag},${elapsed},${size_bytes},${size_mb}" >> "${OUTPUT_FILE}"
done

echo "Metrics saved to ${OUTPUT_FILE}"
