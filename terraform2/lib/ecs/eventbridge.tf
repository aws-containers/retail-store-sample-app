data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

resource "random_id" "eventbridge_suffix" {
  byte_length = 8
}

resource "aws_cloudwatch_log_group" "ecs_container_insights" {
  count = (var.container_insights_setting == "enhanced" && var.lifecycle_events_enabled) ? 1 : 0
  name  = "/aws/events/ecs/containerinsights/${var.environment_name}-cluster/performance"

  tags = {
    ClusterName                      = "${var.environment_name}-cluster"
    "EventBridge-AssociatedRuleName" = "EventsToLogs-retail-${random_id.eventbridge_suffix.hex}"
  }
}

resource "aws_cloudwatch_event_rule" "ecs_events" {
  count       = (var.container_insights_setting == "enhanced" && var.lifecycle_events_enabled) ? 1 : 0
  name        = "EventsToLogs-retail-${random_id.eventbridge_suffix.hex}"
  description = "This rule is used to export to CloudWatch Logs the lifecycle events of the ECS Cluster ${var.environment_name}-cluster."

  event_pattern = jsonencode({
    source = ["aws.ecs"]
    detail = {
      clusterArn = ["arn:aws:ecs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:cluster/${var.environment_name}-cluster"]
    }
  })
}

resource "aws_cloudwatch_event_target" "ecs_events_target" {
  count     = (var.container_insights_setting == "enhanced" && var.lifecycle_events_enabled) ? 1 : 0
  rule      = aws_cloudwatch_event_rule.ecs_events[0].name
  target_id = random_id.eventbridge_suffix.hex
  arn       = aws_cloudwatch_log_group.ecs_container_insights[0].arn
}

resource "aws_cloudwatch_log_resource_policy" "eventbridge_ecs" {
  count       = (var.container_insights_setting == "enhanced" && var.lifecycle_events_enabled) ? 1 : 0
  policy_name = "EventBridge-ECS-ContainerInsights"
  policy_document = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "events.amazonaws.com"
        }
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "${aws_cloudwatch_log_group.ecs_container_insights[0].arn}:*"
      }
    ]
  })
}