module "app_runner_ui" {
  source  = "terraform-aws-modules/app-runner/aws"
  version = "1.2.1"

  service_name = "${var.environment_name}-ui"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          RETAIL_UI_ENDPOINTS_CATALOG  = "https://${aws_apprunner_vpc_ingress_connection.catalog.domain_name}"
          RETAIL_UI_ENDPOINTS_CARTS    = "https://${module.app_runner_carts.vpc_ingress_connection_domain_name}"
          RETAIL_UI_ENDPOINTS_CHECKOUT = "https://${module.app_runner_checkout.vpc_ingress_connection_domain_name}"
          RETAIL_UI_ENDPOINTS_ORDERS   = "https://${aws_apprunner_vpc_ingress_connection.orders.domain_name}"
        }
      }
      image_identifier      = module.container_images.result.ui.url
      image_repository_type = var.image_repository_type
    }
    authentication_configuration = {
      access_role_arn = local.access_role_arn
    }
  }

  create_vpc_connector          = true
  vpc_connector_subnets         = var.subnet_ids
  vpc_connector_security_groups = [aws_security_group.ui.id]

  network_configuration = {
    egress_configuration = {
      egress_type = "VPC"
    }
  }

  tags = var.tags
}

resource "aws_security_group" "ui" {
  name        = "${var.environment_name}-ui"
  description = "Security group for UI component"
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
