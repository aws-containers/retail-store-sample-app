resource "aws_apprunner_service" "catalog" {
  service_name = "${var.environment_name}-catalog"
  
  source_configuration {
    auto_deployments_enabled = false
    image_repository {
      image_configuration {
        port = 8080
        runtime_environment_variables = {
          DB_NAME     = var.catalog_db_name
        }
        runtime_environment_secrets = {
          DB_ENDPOINT = "${aws_secretsmanager_secret.catalog_db.arn}:host::"
          DB_USER     = "${aws_secretsmanager_secret.catalog_db.arn}:username::"
          DB_PASSWORD = "${aws_secretsmanager_secret.catalog_db.arn}:password::"
        }
      }
      image_identifier      = module.container_images.result.catalog
      image_repository_type = "ECR_PUBLIC"
    }
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.catalog.arn
    }
    ingress_configuration {
      is_publicly_accessible = false
    }
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.catalog.arn
  }

  tags = var.tags
}

resource "aws_apprunner_vpc_connector" "catalog" {
  vpc_connector_name = "${var.environment_name}-catalog"
  subnets            = var.subnet_ids
  security_groups    = [aws_security_group.catalog.id]
}

resource "aws_apprunner_vpc_ingress_connection" "catalog" {
  name        = "${var.environment_name}-catalog"
  service_arn = aws_apprunner_service.catalog.arn

  ingress_vpc_configuration {
    vpc_id          = var.vpc_id
    vpc_endpoint_id = module.vpc_endpoints.endpoints["apprunner"].id
  }

  tags = var.tags
}

resource "random_string" "random_catalog_secret" {
  length           = 4
  special          = false
}

resource "aws_secretsmanager_secret" "catalog_db" {
  name       = "${var.environment_name}-catalog-db-${random_string.random_catalog_secret.result}"
  kms_key_id = aws_kms_key.cmk.key_id
}

resource "aws_secretsmanager_secret_version" "catalog_db" {
  secret_id = aws_secretsmanager_secret.catalog_db.id

  secret_string = jsonencode(
    {
      username = var.catalog_db_username
      password = var.catalog_db_password
      host     = "${var.catalog_db_endpoint}:${var.catalog_db_port}"
    }
  )
}

resource "aws_iam_role" "catalog" {
  name = "${var.environment_name}-catalog"

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

data "aws_iam_policy_document" "catalog_db_secret" {
  statement {
    sid = ""
    actions = [
      "secretsmanager:GetSecretValue",
      "kms:Decrypt*"
    ]
    effect    = "Allow"
    resources = [
      aws_secretsmanager_secret.catalog_db.arn,
      aws_kms_key.cmk.arn
    ]
  }
}

resource "aws_iam_role_policy" "catalog_db_secret" {
  name   = "${var.environment_name}-catalog-db-secret"
  role   = aws_iam_role.catalog.id
  policy = data.aws_iam_policy_document.catalog_db_secret.json
}

resource "aws_security_group" "catalog" {
  name        = "${var.environment_name}-catalog"
  description = "Security group for catalog component"
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