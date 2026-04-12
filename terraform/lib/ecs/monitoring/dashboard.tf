locals {
  dashboard_widgets = [
    # Row 1: Service Health Overview
    {
      type   = "metric"
      x      = 0
      y      = 0
      width  = 12
      height = 3
      properties = {
        title   = "Active Alarms"
        metrics = [["AWS/CloudWatch", "NumberOfAlarmsInAlarmState"]]
        view    = "singleValue"
        region  = data.aws_region.current.id
        period  = 60
      }
    },
    # Row 2: Traffic & Errors
    {
      type   = "metric"
      x      = 0
      y      = 3
      width  = 8
      height = 6
      properties = {
        title = "ALB Request Rate"
        metrics = [
          ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", var.alb_arn_suffix, { stat = "Sum", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 8
      y      = 3
      width  = 8
      height = 6
      properties = {
        title = "ALB Error Rate"
        metrics = [
          ["AWS/ApplicationELB", "HTTPCode_ELB_5XX_Count", "LoadBalancer", var.alb_arn_suffix, { stat = "Sum", period = 60 }],
          ["AWS/ApplicationELB", "HTTPCode_Target_5XX_Count", "LoadBalancer", var.alb_arn_suffix, { stat = "Sum", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 16
      y      = 3
      width  = 8
      height = 6
      properties = {
        title = "ALB Latency (P50/P95/P99)"
        metrics = [
          ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", var.alb_arn_suffix, { stat = "p50", period = 60 }],
          ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", var.alb_arn_suffix, { stat = "p95", period = 60 }],
          ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", var.alb_arn_suffix, { stat = "p99", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    # Row 3: ECS Compute
    {
      type   = "metric"
      x      = 0
      y      = 9
      width  = 8
      height = 6
      properties = {
        title = "ECS CPU by Service"
        metrics = [for svc in var.service_names :
          ["AWS/ECS", "CPUUtilization", "ClusterName", var.cluster_name, "ServiceName", svc, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 8
      y      = 9
      width  = 8
      height = 6
      properties = {
        title = "ECS Memory by Service"
        metrics = [for svc in var.service_names :
          ["AWS/ECS", "MemoryUtilization", "ClusterName", var.cluster_name, "ServiceName", svc, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 16
      y      = 9
      width  = 8
      height = 6
      properties = {
        title = "ECS Running Tasks by Service"
        metrics = [for svc in var.service_names :
          ["ECS/ContainerInsights", "RunningTaskCount", "ClusterName", var.cluster_name, "ServiceName", svc, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    # Row 4: Data Stores
    {
      type   = "metric"
      x      = 0
      y      = 15
      width  = 6
      height = 6
      properties = {
        title = "Aurora MySQL (Catalog)"
        metrics = [
          ["AWS/RDS", "CPUUtilization", "DBClusterIdentifier", var.catalog_db_cluster_id, { stat = "Average", period = 60 }],
          ["AWS/RDS", "DatabaseConnections", "DBClusterIdentifier", var.catalog_db_cluster_id, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 6
      y      = 15
      width  = 6
      height = 6
      properties = {
        title = "Aurora PostgreSQL (Orders)"
        metrics = [
          ["AWS/RDS", "CPUUtilization", "DBClusterIdentifier", var.orders_db_cluster_id, { stat = "Average", period = 60 }],
          ["AWS/RDS", "DatabaseConnections", "DBClusterIdentifier", var.orders_db_cluster_id, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 12
      y      = 15
      width  = 6
      height = 6
      properties = {
        title = "DynamoDB (Carts)"
        metrics = [
          ["AWS/DynamoDB", "ConsumedReadCapacityUnits", "TableName", var.carts_dynamodb_table_name, { stat = "Sum", period = 60 }],
          ["AWS/DynamoDB", "ConsumedWriteCapacityUnits", "TableName", var.carts_dynamodb_table_name, { stat = "Sum", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    },
    {
      type   = "metric"
      x      = 18
      y      = 15
      width  = 6
      height = 6
      properties = {
        title = "ElastiCache Redis (Checkout)"
        metrics = [
          ["AWS/ElastiCache", "CPUUtilization", "ReplicationGroupId", var.checkout_elasticache_replication_group_id, { stat = "Average", period = 60 }],
          ["AWS/ElastiCache", "DatabaseMemoryUsagePercentage", "ReplicationGroupId", var.checkout_elasticache_replication_group_id, { stat = "Average", period = 60 }]
        ]
        view   = "timeSeries"
        region = data.aws_region.current.id
      }
    }
  ]
}

data "aws_region" "current" {}

resource "aws_cloudwatch_dashboard" "operations" {
  dashboard_name = "${var.environment_name}-operations"
  dashboard_body = jsonencode({ widgets = local.dashboard_widgets })
}
