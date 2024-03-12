#!/bin/bash

set -e

patch_values() {
  orig=$(yq eval --exit-status '.' $1)
  changed=$(yq eval --exit-status ".image.tag = \"$TAG\"" $1)

  diff_changed=$()

  diff <(echo "$orig") <(echo "$changed") | patch $1
}

export -f patch_values

find deploy/kubernetes/charts/ -name "values.yaml" -not -path "*/opentelemetry/*" -exec bash -c 'patch_values ${0}' '{}' \;
# Clean up dirty patch files
find deploy/kubernetes/charts '('     -name \*-baseline -o     -name \*-merge -o     -name \*-original -o     -name \*.orig -o     -name \*.rej ')' -delete -type f

mkdir -p dist/kubernetes dist/docker-compose

# Template Kubernetes YAML
LOAD_BALANCER=yes helmfile -f deploy/kubernetes/charts/helmfile.yaml template --skip-tests > dist/kubernetes/deploy.yaml

# Template docker-compose
cp deploy/docker-compose/docker-compose.yml dist/docker-compose/docker-compose.yml
yq -i ".services.ui.image = \"public.ecr.aws/aws-containers/retail-store-sample-ui:$TAG\"" dist/docker-compose/docker-compose.yml
yq -i ".services.catalog.image = \"public.ecr.aws/aws-containers/retail-store-sample-catalog:$TAG\"" dist/docker-compose/docker-compose.yml
yq -i ".services.carts.image = \"public.ecr.aws/aws-containers/retail-store-sample-cart:$TAG\"" dist/docker-compose/docker-compose.yml
yq -i ".services.checkout.image = \"public.ecr.aws/aws-containers/retail-store-sample-checkout:$TAG\"" dist/docker-compose/docker-compose.yml
yq -i ".services.orders.image = \"public.ecr.aws/aws-containers/retail-store-sample-orders:$TAG\"" dist/docker-compose/docker-compose.yml
yq -i ".services.assets.image = \"public.ecr.aws/aws-containers/retail-store-sample-assets:$TAG\"" dist/docker-compose/docker-compose.yml

# Template Terraform image default
cat deploy/terraform/lib/images/generated.tf.json | jq -Mr ". | .locals.published_tag = \"$TAG\"" | tee deploy/terraform/lib/images/generated.tf.json