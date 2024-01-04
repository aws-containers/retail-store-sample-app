resource "aws_apprunner_service" "orders" {
  service_name = "${var.environment_name}-orders"

  depends_on = [
    aws_secretsmanager_secret_version.orders_db,
    aws_secretsmanager_secret_version.mq
  ]

  source_configuration {
    auto_deployments_enabled = false
    image_repository {
      image_configuration {
        port = 8080
        runtime_environment_secrets = {
          SPRING_DATASOURCE_URL      = "${aws_secretsmanager_secret.orders_db.arn}:host::"
          SPRING_DATASOURCE_USERNAME = "${aws_secretsmanager_secret.orders_db.arn}:username::"
          SPRING_DATASOURCE_PASSWORD = "${aws_secretsmanager_secret.orders_db.arn}:password::"
          SPRING_RABBITMQ_ADDRESSES  = "${aws_secretsmanager_secret.mq.arn}:host::"
          SPRING_RABBITMQ_USERNAME   = "${aws_secretsmanager_secret.mq.arn}:username::"
          SPRING_RABBITMQ_PASSWORD   = "${aws_secretsmanager_secret.mq.arn}:password::"
        }
        runtime_environment_variables = {
          SPRING_PROFILES_ACTIVE = "rabbitmq"
        }
      }
      image_identifier      = module.container_images.result.orders.url
      image_repository_type = var.image_repository_type
    }

    authentication_configuration {
      access_role_arn = aws_iam_role.ecr_access.arn
    }
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.orders.arn
    }
    ingress_configuration {
      is_publicly_accessible = false
    }
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.orders.arn
  }

  tags = var.tags
}

resource "aws_apprunner_vpc_connector" "orders" {
  vpc_connector_name = "${var.environment_name}-orders"
  subnets            = var.subnet_ids
  security_groups    = [aws_security_group.orders.id]
}

resource "aws_apprunner_vpc_ingress_connection" "orders" {
  name        = "${var.environment_name}-orders"
  service_arn = aws_apprunner_service.orders.arn

  ingress_vpc_configuration {
    vpc_id          = var.vpc_id
    vpc_endpoint_id = module.vpc_endpoints.endpoints["apprunner"].id
  }

  tags = var.tags
}

resource "random_string" "random_orders_secret" {
  length  = 4
  special = false
}

resource "aws_secretsmanager_secret" "orders_db" {
  name = "${var.environment_name}-orders-db-${random_string.random_orders_secret.result}"
}

resource "aws_secretsmanager_secret_version" "orders_db" {
  secret_id = aws_secretsmanager_secret.orders_db.id

  secret_string = jsonencode(
    {
      username = var.orders_db_username
      password = var.orders_db_password
      host     = "jdbc:postgresql://${var.orders_db_endpoint}:${var.orders_db_port}/${var.orders_db_name}"
    }
  )
}

resource "aws_iam_role" "orders" {
  name = "${var.environment_name}-orders"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Sid    = ""
        Principal = {
          Service = "tasks.apprunner.amazonaws.com"
        }
      },
    ]
  })

  tags = var.tags
}

data "aws_iam_policy_document" "orders_db_secret" {
  statement {
    sid = ""
    actions = [
      "secretsmanager:GetSecretValue",
      "kms:Decrypt*"
    ]
    effect = "Allow"
    resources = [
      aws_secretsmanager_secret.orders_db.arn,
      aws_secretsmanager_secret.mq.arn,
      aws_kms_key.cmk.arn
    ]
  }
}

resource "aws_iam_role_policy" "orders_db_secret" {
  name   = "${var.environment_name}-orders-db-secret"
  role   = aws_iam_role.orders.id
  policy = data.aws_iam_policy_document.orders_db_secret.json
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
