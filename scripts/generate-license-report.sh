#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

output_dir="$DIR/../reports/license-report"

if [ -z "$ORT_PATH" ]; then
  echo 'Error: Please set ORT_PATH'
  exit 1
fi

function golicenses()
{
  component=$1

  component_dir="$DIR/../src/$component"

  component_output_dir="$output_dir/$component"

  mkdir -p $component_output_dir

  (cd $component_dir && go-licenses csv . > $component_output_dir/licenses.csv)
}

function ort()
{
  component=$1
  ort=$2

  component_dir="$DIR/../src/$component"

  component_output_dir="$output_dir/$component"

  $ORT_PATH analyze \
    -i $component_dir \
    --repository-configuration-file "$DIR/../src/misc/ort/$ort.ort.yml" \
    -o $component_output_dir -f JSON

  $ORT_PATH report \
    -i $component_output_dir/analyzer-result.json \
    -o $component_output_dir \
    --report-formats NoticeTemplate,StaticHtml
}

if [ -f "$output_dir" ]; then
  echo "Error: Output directory already exists please remove it"
  exit 1
fi

#ort 'ui' 'maven'
#ort 'cart' 'maven'
#ort 'orders' 'maven'
#ort 'checkout' 'npm'

golicenses 'catalog'