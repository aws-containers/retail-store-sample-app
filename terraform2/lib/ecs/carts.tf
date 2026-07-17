module "carts_service" {
  source = "./service"

  environment_name                = var.environment_name
  service_name                    = "carts"
  cluster_arn                     = aws_ecs_cluster.cluster.arn
  vpc_id                          = var.vpc_id
  subnet_ids                      = var.subnet_ids
  tags                            = var.tags
  container_image                 = module.container_images.result.cart.url
  service_discovery_namespace_arn = aws_service_discovery_private_dns_namespace.this.arn
  cloudwatch_logs_group_id        = aws_cloudwatch_log_group.ecs_tasks.id
  healthcheck_path                = "/actuator/health"
  opentelemetry_enabled           = var.opentelemetry_enabled

  environment_variables = {
    RETAIL_CART_PERSISTENCE_PROVIDER            = "dynamodb"
    RETAIL_CART_PERSISTENCE_DYNAMODB_TABLE_NAME = var.carts_dynamodb_table_name
  }

  additional_task_role_iam_policy_arns = [
    var.carts_dynamodb_policy_arn
  ]
}
