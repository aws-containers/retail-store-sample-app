resource "aws_kms_key" "cmk" {
  description             = "${var.environment_name} CMK"
  deletion_window_in_days = 7
}

resource "random_string" "random_mq_secret" {
  length           = 4
  special          = false
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