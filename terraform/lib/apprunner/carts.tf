module "app_runner_carts" {
  source = "terraform-aws-modules/app-runner/aws"

  service_name = "${var.environment_name}-carts"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          CARTS_DYNAMODB_TABLENAME = var.carts_dynamodb_table_name
        }
      }
      image_identifier      = module.container_images.result.cart
      image_repository_type = "ECR_PUBLIC"
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