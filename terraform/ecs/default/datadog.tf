resource "aws_secretsmanager_secret" "datadog_api_key" {
  name = "${var.environment_name}-datadog-api-key"
}

resource "aws_secretsmanager_secret_version" "datadog_api_key" {
  secret_id     = aws_secretsmanager_secret.datadog_api_key.id
  secret_string = var.datadog_api_key
}

