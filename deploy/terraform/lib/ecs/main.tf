module "container_images" {
  source = "../images"

  container_image_overrides = var.container_image_overrides
}

data "aws_ssm_parameter" "fluentbit" {
  name = "/aws/service/aws-for-fluent-bit/stable"
}

data "aws_region" "current" {}
