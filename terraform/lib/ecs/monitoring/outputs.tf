output "critical_sns_topic_arn" {
  description = "ARN of the critical alerts SNS topic"
  value       = aws_sns_topic.critical.arn
}

output "warning_sns_topic_arn" {
  description = "ARN of the warning alerts SNS topic"
  value       = aws_sns_topic.warning.arn
}

output "dashboard_name" {
  description = "Name of the operational dashboard"
  value       = aws_cloudwatch_dashboard.operations.dashboard_name
}
