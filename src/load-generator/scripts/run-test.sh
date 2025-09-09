#!/usr/bin/env bash

set -e

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)

outfile=$(mktemp)

bash $script_dir/run-docker.sh -n app_default -t 'http://ui:8080' -d 30 -o $outfile

result=$(jq '.aggregate.counters."vusers.failed" == 0' $outfile)

if [ "$result" = "false" ]; then
  echo "Check failed: vusers.failed is not 0"
  exit 1
fi

result_200=$(jq '(.aggregate.counters."http.codes.200" // 0) > 0' $outfile)

if [ "$result_200" = "false" ]; then
  echo "Check failed: http.codes.200 is 0"
  exit 1
fi

result_404=$(jq '(.aggregate.counters."http.codes.404" // 0) == 0' $outfile)

if [ "$result_404" = "false" ]; then
  echo "Check failed: http.codes.404 is not 0"
  exit 1
fi