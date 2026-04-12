resource "aws_cloudwatch_metric_alarm" "ecs_cpu_high" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "ECS service ${each.value} CPU > 80%. Runbook: https://wiki.example.com/runbooks/ecs-cpu"
  treat_missing_data  = "notBreaching"

  dimensions = {
    ClusterName = var.cluster_name
    ServiceName = each.value
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  ok_actions    = [aws_sns_topic.warning.arn]
  tags          = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_memory_high" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = 60
  statistic           = "Average"
  threshold           = 85
  alarm_description   = "ECS service ${each.value} memory > 85%. Runbook: https://wiki.example.com/runbooks/ecs-memory"
  treat_missing_data  = "notBreaching"

  dimensions = {
    ClusterName = var.cluster_name
    ServiceName = each.value
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  ok_actions    = [aws_sns_topic.warning.arn]
  tags          = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_running_tasks_low" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-running-tasks-low"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 2
  metric_name         = "RunningTaskCount"
  namespace           = "ECS/ContainerInsights"
  period              = 60
  statistic           = "Average"
  threshold           = 1
  alarm_description   = "ECS service ${each.value} running tasks below desired. Runbook: https://wiki.example.com/runbooks/ecs-tasks"
  treat_missing_data  = "breaching"

  dimensions = {
    ClusterName = var.cluster_name
    ServiceName = each.value
  }

  alarm_actions = [aws_sns_topic.critical.arn]
  ok_actions    = [aws_sns_topic.critical.arn]
  tags          = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_container_unhealthy" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-container-unhealthy"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "UnHealthyContainerHealthStatus"
  namespace           = "ECS/ContainerInsights"
  period              = 60
  statistic           = "Maximum"
  threshold           = 0
  alarm_description   = "ECS service ${each.value} has unhealthy containers. Runbook: https://wiki.example.com/runbooks/ecs-health"
  treat_missing_data  = "notBreaching"

  dimensions = {
    ClusterName = var.cluster_name
    ServiceName = each.value
  }

  alarm_actions = [aws_sns_topic.critical.arn]
  ok_actions    = [aws_sns_topic.critical.arn]
  tags          = var.tags
}
