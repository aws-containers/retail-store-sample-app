resource "aws_cloudwatch_contributor_insight_rule" "top_error_endpoints" {
  rule_name  = "${var.environment_name}-top-error-endpoints"
  rule_state = "ENABLED"

  rule_definition = jsonencode({
    Schema       = { Name = "CloudWatchLogRule", Version = 1 }
    LogGroupNames = [var.log_group_name]
    LogFormat    = "JSON"
    Contribution = {
      Keys = ["$.\"service.name\"", "$.\"http.url\"", "$.\"error.type\""]
      Filters = [
        {
          Match = "$.level"
          In    = ["ERROR"]
        }
      ]
    }
    AggregateOn = "Count"
  })
}

resource "aws_cloudwatch_contributor_insight_rule" "top_slow_requests" {
  count      = var.container_insights_log_group_name != "" ? 1 : 0
  rule_name  = "${var.environment_name}-top-slow-requests"
  rule_state = "ENABLED"

  rule_definition = jsonencode({
    Schema       = { Name = "CloudWatchLogRule", Version = 1 }
    LogGroupNames = [var.container_insights_log_group_name]
    LogFormat    = "JSON"
    Contribution = {
      Keys = ["$.TaskId", "$.ServiceName"]
      Filters = [
        {
          Match     = "$.Duration"
          GreaterThan = 2000
        }
      ]
    }
    AggregateOn = "Count"
  })
}

resource "aws_cloudwatch_contributor_insight_rule" "top_request_sources" {
  rule_name  = "${var.environment_name}-top-request-sources"
  rule_state = "ENABLED"

  rule_definition = jsonencode({
    Schema       = { Name = "CloudWatchLogRule", Version = 1 }
    LogGroupNames = [var.log_group_name]
    LogFormat    = "JSON"
    Contribution = {
      Keys = ["$.\"http.client_ip\"", "$.\"http.user_agent\""]
      Filters = [
        {
          Match = "$.\"http.client_ip\""
          IsPresent = true
        }
      ]
    }
    AggregateOn = "Count"
  })
}
