#!/bin/bash

set -euo pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

output_dir="$DIR/../reports/license-report"

function golicenses()
{
  component=$1

  component_dir="$DIR/../src/$component"

  component_output_dir="$output_dir/$component"

  mkdir -p $component_output_dir

  (cd $component_dir && go-licenses csv . > $component_output_dir/licenses.csv)
}

function run_ort()
{
  component=$1
  profile=$2

  component_dir="$DIR/../src/$component"

  component_output_dir="$output_dir/$component"

  ort analyze \
    -i $component_dir \
    --repository-configuration-file "$DIR/../src/misc/ort/$profile.ort.yml" \
    --package-curations-file $DIR/../src/misc/ort/curations.yml \
    -o $component_output_dir -f JSON

  ort report \
    -i $component_output_dir/analyzer-result.json \
    -o $component_output_dir \
    --report-formats NoticeTemplate,StaticHtml

  ort --debug download \
    -i $component_output_dir/analyzer-result.json \
     --package-types PACKAGE \
    -o $component_output_dir/src
}

if [ -f "$output_dir" ]; then
  echo "Error: Output directory already exists please remove it"
  exit 1
fi

#run_ort 'ui' 'maven'
#run_ort 'cart' 'maven'
#run_ort 'orders' 'maven'
#run_ort 'checkout' 'npm'

golicenses 'catalog'