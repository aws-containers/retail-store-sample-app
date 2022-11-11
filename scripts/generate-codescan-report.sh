#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

output_dir="$DIR/../reports/code-scan"

mkdir -p $output_dir

semgrep --config=auto \
  src/catalog src/cart src/ui src/checkout src/orders \
  -o $output_dir/code-scan-report.txt