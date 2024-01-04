module "orders_service" {
  source = "./service"

  environment_name                = var.environment_name
  service_name                    = "orders"
  cluster_arn                     = aws_ecs_cluster.cluster.arn
  vpc_id                          = var.vpc_id
  vpc_cidr                        = var.vpc_cidr
  subnet_ids                      = var.subnet_ids
  public_subnet_ids               = var.public_subnet_ids
  tags                            = var.tags
  container_image                 = module.container_images.result.orders.url
  service_discovery_namespace_arn = aws_service_discovery_private_dns_namespace.this.arn
  cloudwatch_logs_group_id        = aws_cloudwatch_log_group.ecs_tasks.id
  healthcheck_path                = "/actuator/health"

  environment_variables = {
    SPRING_PROFILES_ACTIVE = "rabbitmq"
  }

  secrets = {
    SPRING_DATASOURCE_URL      = "${aws_secretsmanager_secret_version.orders_db.arn}:host::"
    SPRING_DATASOURCE_USERNAME = "${aws_secretsmanager_secret_version.orders_db.arn}:username::"
    SPRING_DATASOURCE_PASSWORD = "${aws_secretsmanager_secret_version.orders_db.arn}:password::"
    SPRING_RABBITMQ_ADDRESSES  = "${aws_secretsmanager_secret_version.mq.arn}:host::"
    SPRING_RABBITMQ_USERNAME   = "${aws_secretsmanager_secret_version.mq.arn}:username::"
    SPRING_RABBITMQ_PASSWORD   = "${aws_secretsmanager_secret_version.mq.arn}:password::"
  }

  additional_task_execution_role_iam_policy_arns = [
    aws_iam_policy.orders_policy.arn
  ]
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

resource "aws_iam_policy" "orders_policy" {
  name        = "${var.environment_name}-orders"
  path        = "/"
  description = "Policy for orders"

  policy = data.aws_iam_policy_document.orders_db_secret.json
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
