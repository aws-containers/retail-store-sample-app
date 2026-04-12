# --- Error Log Filters (FR-5.1) ---

resource "aws_cloudwatch_log_metric_filter" "error_count" {
  name           = "${var.environment_name}-error-count"
  log_group_name = var.log_group_name
  pattern        = "{ $.level = \"ERROR\" }"

  metric_transformation {
    name          = "ErrorCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "exception_count" {
  name           = "${var.environment_name}-exception-count"
  log_group_name = var.log_group_name
  pattern        = "?Exception ?exception ?Error ?error"

  metric_transformation {
    name          = "ExceptionCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "fatal_count" {
  name           = "${var.environment_name}-fatal-count"
  log_group_name = var.log_group_name
  pattern        = "{ $.level = \"FATAL\" }"

  metric_transformation {
    name          = "FatalCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

# --- Application-Specific Filters (FR-5.2) ---

resource "aws_cloudwatch_log_metric_filter" "payment_failures" {
  name           = "${var.environment_name}-payment-failures"
  log_group_name = var.log_group_name
  pattern        = "\"payment failed\""

  metric_transformation {
    name          = "PaymentFailureCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "database_errors" {
  name           = "${var.environment_name}-database-errors"
  log_group_name = var.log_group_name
  pattern        = "?\"connection refused\" ?\"connection timeout\" ?\"deadlock\""

  metric_transformation {
    name          = "DatabaseErrorCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "auth_failures" {
  name           = "${var.environment_name}-auth-failures"
  log_group_name = var.log_group_name
  pattern        = "?\"authentication failed\" ?\"unauthorized\" ?\"403\""

  metric_transformation {
    name          = "AuthFailureCount"
    namespace     = "${var.environment_name}/ApplicationLogs"
    value         = "1"
    default_value = "0"
  }
}

# --- Metric Filter Alarms (FR-5.3) ---

resource "aws_cloudwatch_metric_alarm" "error_count_high" {
  alarm_name          = "${var.environment_name}-error-count-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ErrorCount"
  namespace           = "${var.environment_name}/ApplicationLogs"
  period              = 60
  statistic           = "Sum"
  threshold           = 10
  alarm_description   = "Error count > 10 per minute"
  treat_missing_data  = "notBreaching"
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "exception_count_high" {
  alarm_name          = "${var.environment_name}-exception-count-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ExceptionCount"
  namespace           = "${var.environment_name}/ApplicationLogs"
  period              = 60
  statistic           = "Sum"
  threshold           = 5
  alarm_description   = "Exception count > 5 per minute"
  treat_missing_data  = "notBreaching"
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "payment_failures_alarm" {
  alarm_name          = "${var.environment_name}-payment-failures"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "PaymentFailureCount"
  namespace           = "${var.environment_name}/ApplicationLogs"
  period              = 300
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Payment failures detected"
  treat_missing_data  = "notBreaching"
  alarm_actions       = [aws_sns_topic.critical.arn]
  ok_actions          = [aws_sns_topic.critical.arn]
  tags                = var.tags
}
