resource "aws_xray_group" "cluster" {
  group_name        = "${var.environment_name}-cluster"
  filter_expression = "service(\"${var.cluster_name}\")"

  insights_configuration {
    insights_enabled          = true
    notifications_enabled     = true
  }

  tags = var.tags
}

resource "aws_xray_sampling_rule" "errors" {
  rule_name      = "${var.environment_name}-errors"
  priority       = 100
  reservoir_size = 10
  fixed_rate     = 1.0
  http_method    = "*"
  host           = "*"
  url_path       = "*"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"
  version        = 1

  tags = var.tags
}

resource "aws_xray_sampling_rule" "slow_requests" {
  rule_name      = "${var.environment_name}-slow-requests"
  priority       = 200
  reservoir_size = 10
  fixed_rate     = 1.0
  http_method    = "*"
  host           = "*"
  url_path       = "*"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"
  version        = 1

  tags = var.tags
}

resource "aws_xray_sampling_rule" "baseline" {
  rule_name      = "${var.environment_name}-baseline"
  priority       = 1000
  reservoir_size = 5
  fixed_rate     = 0.25
  http_method    = "*"
  host           = "*"
  url_path       = "*"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"
  version        = 1

  tags = var.tags
}
