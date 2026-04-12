resource "aws_cloudwatch_log_index_policy" "application_logs" {
  log_group_name = var.log_group_name

  policy_document = jsonencode({
    Fields = ["service.name", "level", "http.status_code", "error.type"]
  })
}
