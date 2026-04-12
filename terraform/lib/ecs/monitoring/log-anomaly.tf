resource "aws_cloudwatch_log_anomaly_detector" "application_logs" {
  detector_name           = "${var.environment_name}-app-logs-anomaly"
  log_group_arn_list      = [var.log_group_arn]
  anomaly_visibility_time = 7
  evaluation_frequency    = "FIVE_MIN"
  enabled                 = true
}

resource "aws_cloudwatch_log_anomaly_detector" "container_insights" {
  count                   = var.container_insights_log_group_arn != "" ? 1 : 0
  detector_name           = "${var.environment_name}-container-insights-anomaly"
  log_group_arn_list      = [var.container_insights_log_group_arn]
  anomaly_visibility_time = 7
  evaluation_frequency    = "TEN_MIN"
  enabled                 = true
}
