# --- Aurora MySQL (catalog) ---

resource "aws_cloudwatch_metric_alarm" "aurora_mysql_cpu" {
  alarm_name          = "${var.environment_name}-aurora-mysql-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Aurora MySQL CPU > 80%"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.catalog_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_mysql_connections" {
  alarm_name          = "${var.environment_name}-aurora-mysql-connections-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Aurora MySQL connections high"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.catalog_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_mysql_read_latency" {
  alarm_name          = "${var.environment_name}-aurora-mysql-read-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "ReadLatency"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 0.02
  alarm_description   = "Aurora MySQL read latency > 20ms"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.catalog_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_mysql_write_latency" {
  alarm_name          = "${var.environment_name}-aurora-mysql-write-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "WriteLatency"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 0.05
  alarm_description   = "Aurora MySQL write latency > 50ms"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.catalog_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

# --- Aurora PostgreSQL (orders) ---

resource "aws_cloudwatch_metric_alarm" "aurora_pg_cpu" {
  alarm_name          = "${var.environment_name}-aurora-pg-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Aurora PostgreSQL CPU > 80%"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.orders_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_pg_connections" {
  alarm_name          = "${var.environment_name}-aurora-pg-connections-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Aurora PostgreSQL connections high"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.orders_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_pg_read_latency" {
  alarm_name          = "${var.environment_name}-aurora-pg-read-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "ReadLatency"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 0.02
  alarm_description   = "Aurora PostgreSQL read latency > 20ms"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.orders_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "aurora_pg_write_latency" {
  alarm_name          = "${var.environment_name}-aurora-pg-write-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "WriteLatency"
  namespace           = "AWS/RDS"
  period              = 60
  statistic           = "Average"
  threshold           = 0.05
  alarm_description   = "Aurora PostgreSQL write latency > 50ms"
  treat_missing_data  = "notBreaching"
  dimensions          = { DBClusterIdentifier = var.orders_db_cluster_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

# --- DynamoDB (carts) ---

resource "aws_cloudwatch_metric_alarm" "dynamodb_read_throttles" {
  alarm_name          = "${var.environment_name}-dynamodb-read-throttles"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ReadThrottleEvents"
  namespace           = "AWS/DynamoDB"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "DynamoDB read throttling detected"
  treat_missing_data  = "notBreaching"
  dimensions          = { TableName = var.carts_dynamodb_table_name }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "dynamodb_write_throttles" {
  alarm_name          = "${var.environment_name}-dynamodb-write-throttles"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "WriteThrottleEvents"
  namespace           = "AWS/DynamoDB"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "DynamoDB write throttling detected"
  treat_missing_data  = "notBreaching"
  dimensions          = { TableName = var.carts_dynamodb_table_name }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "dynamodb_system_errors" {
  alarm_name          = "${var.environment_name}-dynamodb-system-errors"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "SystemErrors"
  namespace           = "AWS/DynamoDB"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "DynamoDB system errors detected"
  treat_missing_data  = "notBreaching"
  dimensions          = { TableName = var.carts_dynamodb_table_name }
  alarm_actions       = [aws_sns_topic.critical.arn]
  ok_actions          = [aws_sns_topic.critical.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "dynamodb_latency_high" {
  alarm_name          = "${var.environment_name}-dynamodb-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "SuccessfulRequestLatency"
  namespace           = "AWS/DynamoDB"
  period              = 60
  statistic           = "Average"
  threshold           = 50
  alarm_description   = "DynamoDB latency > 50ms"
  treat_missing_data  = "notBreaching"
  dimensions          = { TableName = var.carts_dynamodb_table_name }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

# --- ElastiCache Redis (checkout) ---

resource "aws_cloudwatch_metric_alarm" "redis_cpu_high" {
  alarm_name          = "${var.environment_name}-redis-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ElastiCache"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Redis CPU > 80%"
  treat_missing_data  = "notBreaching"
  dimensions          = { ReplicationGroupId = var.checkout_elasticache_replication_group_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "redis_memory_high" {
  alarm_name          = "${var.environment_name}-redis-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "DatabaseMemoryUsagePercentage"
  namespace           = "AWS/ElastiCache"
  period              = 60
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Redis memory > 80%"
  treat_missing_data  = "notBreaching"
  dimensions          = { ReplicationGroupId = var.checkout_elasticache_replication_group_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "redis_evictions" {
  alarm_name          = "${var.environment_name}-redis-evictions"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "Evictions"
  namespace           = "AWS/ElastiCache"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Redis evictions detected"
  treat_missing_data  = "notBreaching"
  dimensions          = { ReplicationGroupId = var.checkout_elasticache_replication_group_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}

resource "aws_cloudwatch_metric_alarm" "redis_connections_high" {
  alarm_name          = "${var.environment_name}-redis-connections-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "CurrConnections"
  namespace           = "AWS/ElastiCache"
  period              = 60
  statistic           = "Average"
  threshold           = 100
  alarm_description   = "Redis connections > 100"
  treat_missing_data  = "notBreaching"
  dimensions          = { ReplicationGroupId = var.checkout_elasticache_replication_group_id }
  alarm_actions       = [aws_sns_topic.warning.arn]
  ok_actions          = [aws_sns_topic.warning.arn]
  tags                = var.tags
}
