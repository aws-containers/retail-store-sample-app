#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

output_dir="$DIR/../reports/security-scan"

mkdir -p $output_dir

# Build images so we can use Trivy
$DIR/build-image.sh -t scan

trivy image watchn/catalog:scan --security-checks vuln -o $output_dir/catalog.txt
trivy image watchn/assets:scan --security-checks vuln -o $output_dir/assets.txt
trivy image watchn/ui:scan --security-checks vuln -o $output_dir/ui.txt
trivy image watchn/checkout:scan --security-checks vuln -o $output_dir/checkout.txt
trivy image watchn/cart:scan --security-checks vuln -o $output_dir/cart.txt
trivy image watchn/orders:scan --security-checks vuln -o $output_dir/orders.txt