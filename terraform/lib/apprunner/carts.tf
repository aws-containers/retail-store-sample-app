# tflint-ignore: terraform_module_version
module "app_runner_carts" {
  source  = "terraform-aws-modules/app-runner/aws"
  version = "1.2.1"

  service_name = "${var.environment_name}-carts"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          RETAIL_CART_PERSISTENCE_PROVIDER            = "dynamodb"
          RETAIL_CART_PERSISTENCE_DYNAMODB_TABLE_NAME = var.carts_dynamodb_table_name
        }
      }
      image_identifier      = module.container_images.result.cart.url
      image_repository_type = var.image_repository_type
    }
    authentication_configuration = {
      access_role_arn = local.access_role_arn
    }
  }

  create_ingress_vpc_connection = true
  ingress_vpc_id                = var.vpc_id
  ingress_vpc_endpoint_id       = module.vpc_endpoints.endpoints["apprunner"].id

  create_vpc_connector          = true
  vpc_connector_subnets         = var.subnet_ids
  vpc_connector_security_groups = [aws_security_group.carts.id]

  network_configuration = {
    egress_configuration = {
      egress_type = "VPC"
    }
    ingress_configuration = {
      is_publicly_accessible = false
    }
  }

  instance_iam_role_policies = {
    "dynamo" = var.carts_dynamodb_policy_arn
  }

  tags = var.tags
}

resource "aws_security_group" "carts" {
  name        = "${var.environment_name}-carts"
  description = "Security group for carts component"
  vpc_id      = var.vpc_id

  egress {
    description = "Allow all egress"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}
