module "app_runner_ui" {
  source = "terraform-aws-modules/app-runner/aws"

  service_name = "${var.environment_name}-ui"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          ENDPOINTS_CATALOG  = "https://${aws_apprunner_vpc_ingress_connection.catalog.domain_name}"
          ENDPOINTS_CARTS    = "https://${module.app_runner_carts.vpc_ingress_connection_domain_name}"
          ENDPOINTS_CHECKOUT = "https://${module.app_runner_checkout.vpc_ingress_connection_domain_name}"
          ENDPOINTS_ORDERS   = "https://${aws_apprunner_vpc_ingress_connection.orders.domain_name}"
          ENDPOINTS_ASSETS   = "https://${module.app_runner_assets.vpc_ingress_connection_domain_name}"
        }
      }
      image_identifier      = module.container_images.result.ui
      image_repository_type = "ECR_PUBLIC"
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