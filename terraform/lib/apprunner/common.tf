resource "aws_kms_key" "cmk" {
  description             = "${var.environment_name} CMK"
  deletion_window_in_days = 7
}

resource "random_string" "random_mq_secret" {
  length  = 4
  special = false
}

resource "aws_secretsmanager_secret" "mq" {
  name = "${var.environment_name}-mq-${random_string.random_mq_secret.result}"
}

resource "aws_secretsmanager_secret_version" "mq" {
  secret_id = aws_secretsmanager_secret.mq.id

  secret_string = jsonencode(
    {
      username = var.mq_username
      password = var.mq_password
      host     = var.mq_endpoint
    }
  )
}

resource "aws_iam_role" "ecr_access" {
  name = "${var.environment_name}-ecr-access"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Sid    = ""
        Principal = {
          Service = "build.apprunner.amazonaws.com"
        }
      },
    ]
  })

  tags = var.tags
}

data "aws_iam_policy" "ecr_access" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

resource "aws_iam_role_policy_attachment" "ecr_access" {
  role       = aws_iam_role.ecr_access.name
  policy_arn = data.aws_iam_policy.ecr_access.arn
}

locals {
  access_role_arn = var.image_repository_type == "ECR_PUBLIC" ? null : aws_iam_role.ecr_access.arn
}