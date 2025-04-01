module "checkout_service" {
  source = "./service"

  environment_name                = var.environment_name
  service_name                    = "checkout"
  cluster_arn                     = aws_ecs_cluster.cluster.arn
  vpc_id                          = var.vpc_id
  subnet_ids                      = var.subnet_ids
  tags                            = var.tags
  container_image                 = module.container_images.result.checkout.url
  service_discovery_namespace_arn = aws_service_discovery_private_dns_namespace.this.arn
  cloudwatch_logs_group_id        = aws_cloudwatch_log_group.ecs_tasks.id
  opentelemetry_enabled           = var.opentelemetry_enabled

  environment_variables = {
    RETAIL_CHECKOUT_PERSISTENCE_PROVIDER  = "redis"
    RETAIL_CHECKOUT_PERSISTENCE_REDIS_URL = "redis://${var.checkout_redis_endpoint}:${var.checkout_redis_port}"
    RETAIL_CHECKOUT_ENDPOINTS_ORDERS      = "http://${module.orders_service.ecs_service_name}"
  }

  additional_task_role_iam_policy_arns = [
    var.carts_dynamodb_policy_arn
  ]
}
