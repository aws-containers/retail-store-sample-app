#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

output_dir="$DIR/../reports/security-scan"

mkdir -p $output_dir

# Build images so we can use Trivy
$DIR/build-image.sh -t scan

trivy image aws-containers/retail-store-sample-catalog:scan --security-checks vuln -o $output_dir/catalog.txt
trivy image aws-containers/retail-store-sample-assets:scan --security-checks vuln -o $output_dir/assets.txt
trivy image aws-containers/retail-store-sample-ui:scan --security-checks vuln -o $output_dir/ui.txt
trivy image aws-containers/retail-store-sample-checkout:scan --security-checks vuln -o $output_dir/checkout.txt
trivy image aws-containers/retail-store-sample-cart:scan --security-checks vuln -o $output_dir/cart.txt
trivy image aws-containers/retail-store-sample-orders:scan --security-checks vuln -o $output_dir/orders.txt