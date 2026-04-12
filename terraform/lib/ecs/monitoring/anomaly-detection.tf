# --- ALB Anomaly Detection Alarms (FR-7) ---

resource "aws_cloudwatch_metric_alarm" "alb_5xx_anomaly" {
  alarm_name          = "${var.environment_name}-alb-5xx-anomaly"
  comparison_operator = "GreaterThanUpperThreshold"
  evaluation_periods  = 3
  threshold_metric_id = "ad1"
  alarm_description   = "ALB 5XX anomaly detected"
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "ad1"
    return_data = true
    expression  = "ANOMALY_DETECTION_BAND(m1, 2)"
  }
  metric_query {
    id          = "m1"
    return_data = true
    metric {
      metric_name = "HTTPCode_ELB_5XX_Count"
      namespace   = "AWS/ApplicationELB"
      period      = 300
      stat        = "Sum"
      dimensions  = { LoadBalancer = var.alb_arn_suffix }
    }
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  tags          = var.tags
}

resource "aws_cloudwatch_metric_alarm" "alb_latency_anomaly" {
  alarm_name          = "${var.environment_name}-alb-latency-anomaly"
  comparison_operator = "GreaterThanUpperThreshold"
  evaluation_periods  = 3
  threshold_metric_id = "ad1"
  alarm_description   = "ALB latency anomaly detected"
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "ad1"
    return_data = true
    expression  = "ANOMALY_DETECTION_BAND(m1, 2)"
  }
  metric_query {
    id          = "m1"
    return_data = true
    metric {
      metric_name = "TargetResponseTime"
      namespace   = "AWS/ApplicationELB"
      period      = 300
      stat        = "Average"
      dimensions  = { LoadBalancer = var.alb_arn_suffix }
    }
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  tags          = var.tags
}

# --- ECS Per-Service Anomaly Detection (FR-7) ---

resource "aws_cloudwatch_metric_alarm" "ecs_cpu_anomaly" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-cpu-anomaly"
  comparison_operator = "GreaterThanUpperThreshold"
  evaluation_periods  = 3
  threshold_metric_id = "ad1"
  alarm_description   = "ECS ${each.value} CPU anomaly detected"
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "ad1"
    return_data = true
    expression  = "ANOMALY_DETECTION_BAND(m1, 2)"
  }
  metric_query {
    id          = "m1"
    return_data = true
    metric {
      metric_name = "CPUUtilization"
      namespace   = "AWS/ECS"
      period      = 300
      stat        = "Average"
      dimensions = {
        ClusterName = var.cluster_name
        ServiceName = each.value
      }
    }
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  tags          = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_memory_anomaly" {
  for_each = toset(var.service_names)

  alarm_name          = "${var.environment_name}-${each.value}-memory-anomaly"
  comparison_operator = "GreaterThanUpperThreshold"
  evaluation_periods  = 3
  threshold_metric_id = "ad1"
  alarm_description   = "ECS ${each.value} memory anomaly detected"
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "ad1"
    return_data = true
    expression  = "ANOMALY_DETECTION_BAND(m1, 2)"
  }
  metric_query {
    id          = "m1"
    return_data = true
    metric {
      metric_name = "MemoryUtilization"
      namespace   = "AWS/ECS"
      period      = 300
      stat        = "Average"
      dimensions = {
        ClusterName = var.cluster_name
        ServiceName = each.value
      }
    }
  }

  alarm_actions = [aws_sns_topic.warning.arn]
  tags          = var.tags
}
