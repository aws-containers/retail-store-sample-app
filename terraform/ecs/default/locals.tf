locals {
  account_id = var.account_id

  datadog_container_def = {
    name  = "datadog-agent"
    image = "public.ecr.aws/datadog/agent:latest"
    environment = [
      {
        name  = "DD_ECS_TASK_COLLECTION_ENABLED"
        value = "true"
      },
      {
        name  = "DD_APM_ENABLED"
        value = "true"
      },
      {
        name  = "DD_SITE"
        value = "datadoghq.com"
      }
    ]
    secrets = [
      {
        name      = "DD_API_KEY"
        valueFrom = aws_secretsmanager_secret.datadog_api_key.arn
      }
    ]
  }
}


