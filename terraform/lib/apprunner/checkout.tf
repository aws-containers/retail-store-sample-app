module "app_runner_checkout" {
  source = "terraform-aws-modules/app-runner/aws"

  service_name = "${var.environment_name}-checkout"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          REDIS_URL        = "redis://${var.checkout_redis_endpoint}:${var.checkout_redis_port}"
          ENDPOINTS_ORDERS = "https://${aws_apprunner_vpc_ingress_connection.orders.domain_name}"
        }
      }
      image_identifier      = module.container_images.result.checkout
      image_repository_type = "ECR_PUBLIC"
    }
  }

  create_ingress_vpc_connection = true
  ingress_vpc_id                = var.vpc_id
  ingress_vpc_endpoint_id       = module.vpc_endpoints.endpoints["apprunner"].id

  create_vpc_connector          = true
  vpc_connector_subnets         = var.subnet_ids
  vpc_connector_security_groups = [aws_security_group.checkout.id]

  network_configuration = {
    egress_configuration = {
      egress_type = "VPC"
    }
    ingress_configuration = {
      is_publicly_accessible = false
    }
  }

  tags = var.tags
}

resource "aws_security_group" "checkout" {
  name        = "${var.environment_name}-checkout"
  description = "Security group for checkout component"
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