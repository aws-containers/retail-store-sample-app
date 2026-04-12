module "monitoring" {
  count  = var.monitoring_enabled ? 1 : 0
  source = "./monitoring"

  environment_name = var.environment_name
  cluster_name     = aws_ecs_cluster.cluster.name
  service_names    = ["ui", "catalog", "carts", "orders", "checkout"]

  alb_arn_suffix          = module.alb.lb_arn_suffix
  target_group_arn_suffix = module.alb.target_group_arn_suffixes[0]

  log_group_name = aws_cloudwatch_log_group.ecs_tasks.name
  log_group_arn  = aws_cloudwatch_log_group.ecs_tasks.arn

  container_insights_log_group_name = var.lifecycle_events_enabled ? aws_cloudwatch_log_group.ecs_container_insights[0].name : ""
  container_insights_log_group_arn  = var.lifecycle_events_enabled ? aws_cloudwatch_log_group.ecs_container_insights[0].arn : ""

  catalog_db_cluster_id                    = "${var.environment_name}-catalog"
  orders_db_cluster_id                     = "${var.environment_name}-orders"
  carts_dynamodb_table_name                = var.carts_dynamodb_table_name
  checkout_elasticache_replication_group_id = "${var.environment_name}-checkout"

  alert_email = var.alert_email
  tags        = var.tags
}
