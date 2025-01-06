# AWS Containers Retail Sample - Images Terraform module

This Terraform module is designed to provide a reusable mechanism to generate URLs for the container images that should be used with the ability to override both coarse and fine-grained where needed. This also provides a central location that can be updated on release to upgrade all the Terraform modules.

Calling this module with no inputs will return the default images for the version of the Terraform configuration:

```hcl
module "images" {
  source = "lib/images"
}

output "container_images" {
  value = module.images.result
}
```

Applying this will output:

```
Outputs:

result = {
  "assets" = "public.ecr.aws/aws-containers/retail-store-sample-assets:0.2.0"
  "cart" = "public.ecr.aws/aws-containers/retail-store-sample-cart:0.2.0"
  "catalog" = "public.ecr.aws/aws-containers/retail-store-sample-catalog:0.2.0"
  "checkout" = "public.ecr.aws/aws-containers/retail-store-sample-checkout:0.2.0"
  "orders" = "public.ecr.aws/aws-containers/retail-store-sample-orders:0.2.0"
  "ui" = "public.ecr.aws/aws-containers/retail-store-sample-ui:0.2.0"
}
```

## Override default tag

You can override the default image tag used like so:

```hcl
module "images" {
  source = "lib/images"

  container_image_overrides = {
    default_tag = "0.1.0"
  }
}

output "container_images" {
  value = module.images.result
}
```

Applying this will output:

```
Outputs:

result = {
  "assets" = "public.ecr.aws/aws-containers/retail-store-sample-assets:0.1.0"
  "cart" = "public.ecr.aws/aws-containers/retail-store-sample-cart:0.1.0"
  "catalog" = "public.ecr.aws/aws-containers/retail-store-sample-catalog:0.1.0"
  "checkout" = "public.ecr.aws/aws-containers/retail-store-sample-checkout:0.1.0"
  "orders" = "public.ecr.aws/aws-containers/retail-store-sample-orders:0.1.0"
  "ui" = "public.ecr.aws/aws-containers/retail-store-sample-ui:0.1.0"
}
```

This has changed all of the images from the version which matches the Terraform configuration to `0.1.0`.

## Override default container registry

By default the URLs generated will use the images hosted in ECR Public. However in some cases you might want to host your own version of the images in something like ECR private. You reconfigure all the images like so:

```hcl
module "images" {
  source = "lib/images"

  container_image_overrides = {
    default_registry = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy"
  }
}

output "container_images" {
  value = module.images.result
}
```

Applying this will output:

```
Outputs:

result = {
  "assets" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-assets:0.1.0"
  "cart" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-cart:0.1.0"
  "catalog" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-catalog:0.1.0"
  "checkout" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-checkout:0.1.0"
  "orders" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-orders:0.1.0"
  "ui" = "111111111.dkr.ecr.us-west-2.amazonaws.com/retail-store-sample-copy/retail-store-sample-ui:0.1.0"
}
```

## Override individual images

You can entirely override each image URL like so:

```hcl
module "images" {
  source = "lib/images"

  container_image_overrides = {
    assets = "my-assets-image:v1"
  }
}

output "container_images" {
  value = module.images.result
}
```

Applying this will output:

```
Outputs:

result = {
  "assets" = "my-assets-image:v1"
  "cart" = "public.ecr.aws/aws-containers/retail-store-sample-cart:0.2.0"
  "catalog" = "public.ecr.aws/aws-containers/retail-store-sample-catalog:0.2.0"
  "checkout" = "public.ecr.aws/aws-containers/retail-store-sample-checkout:0.2.0"
  "orders" = "public.ecr.aws/aws-containers/retail-store-sample-orders:0.2.0"
  "ui" = "public.ecr.aws/aws-containers/retail-store-sample-ui:0.2.0"
}
```