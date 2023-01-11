module "app_runner_orders" {
  source = "terraform-aws-modules/app-runner/aws"

  service_name = "${var.environment_name}-orders"

  source_configuration = {
    auto_deployments_enabled = false
    image_repository = {
      image_configuration = {
        port = 8080
        runtime_environment_variables = {
          SPRING_DATASOURCE_WRITER_URL      = "jdbc:mariadb://${var.orders_db_endpoint}:${var.orders_db_port}/${var.orders_db_name}"
          SPRING_DATASOURCE_WRITER_USERNAME = var.orders_db_username
          SPRING_DATASOURCE_WRITER_PASSWORD = var.orders_db_password
          SPRING_DATASOURCE_READER_URL      = "jdbc:mariadb://${var.orders_db_endpoint}:${var.orders_db_port}/${var.orders_db_name}"
          SPRING_DATASOURCE_READER_USERNAME = var.orders_db_username
          SPRING_DATASOURCE_READER_PASSWORD = var.orders_db_password
          SPRING_RABBITMQ_ADDRESSES         = var.mq_endpoint
          SPRING_RABBITMQ_USER              = var.mq_username
          SPRING_RABBITMQ_PASSWORD          = var.mq_password
        }
      }
      image_identifier      = module.container_images.result.orders
      image_repository_type = "ECR_PUBLIC"
    }
  }

  create_ingress_vpc_connection = true
  ingress_vpc_id                = var.vpc_id
  ingress_vpc_endpoint_id       = module.vpc_endpoints.endpoints["apprunner"].id

  create_vpc_connector          = true
  vpc_connector_subnets         = var.subnet_ids
  vpc_connector_security_groups = [aws_security_group.orders.id]

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

resource "aws_security_group" "orders" {
  name        = "${var.environment_name}-orders"
  description = "Security group for orders component"
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