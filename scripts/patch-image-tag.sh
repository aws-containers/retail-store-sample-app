#!/bin/bash

set -e

patch_values() {
  orig=$(yq eval --exit-status '.' $1)
  changed=$(yq eval --exit-status ".image.tag = \"$TAG\"" $1)

  diff_changed=$()

  diff <(echo "$orig") <(echo "$changed") | patch $1
}

patch_chart() {
  orig=$(yq eval --exit-status '.' $1)
  changed=$(yq eval --exit-status ".version = \"$TAG\"" $1)

  diff_changed=$()

  diff <(echo "$orig") <(echo "$changed") | patch $1
}

export -f patch_values
export -f patch_chart

find deploy/kubernetes/charts/ -name "values.yaml" -not -path "*/opentelemetry/*" -not -path "*/app/*" -exec bash -c 'patch_values ${0}' '{}' \;
find deploy/kubernetes/charts/ -name "Chart.yaml" -not -path "*/opentelemetry/*" -exec bash -c 'patch_chart ${0}' '{}' \;

outfile=$(mktemp)

yq eval ".dependencies[].version |= \"$TAG\"" deploy/kubernetes/charts/app/Chart.yaml > $outfile
cp $outfile deploy/kubernetes/charts/app/Chart.yaml

# Clean up dirty patch files
find deploy/kubernetes/charts '('     -name \*-baseline -o     -name \*-merge -o     -name \*-original -o     -name \*.orig -o     -name \*.rej ')' -delete -type f