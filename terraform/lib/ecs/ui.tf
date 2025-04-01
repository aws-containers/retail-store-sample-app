module "ui_service" {
  source = "./service"

  environment_name                = var.environment_name
  service_name                    = "ui"
  cluster_arn                     = aws_ecs_cluster.cluster.arn
  vpc_id                          = var.vpc_id
  subnet_ids                      = var.subnet_ids
  tags                            = var.tags
  container_image                 = module.container_images.result.ui.url
  service_discovery_namespace_arn = aws_service_discovery_private_dns_namespace.this.arn
  cloudwatch_logs_group_id        = aws_cloudwatch_log_group.ecs_tasks.id
  healthcheck_path                = "/actuator/health"
  alb_target_group_arn            = element(module.alb.target_group_arns, 0)
  opentelemetry_enabled           = var.opentelemetry_enabled


  environment_variables = {
    RETAIL_UI_ENDPOINTS_CATALOG  = "http://${module.catalog_service.ecs_service_name}"
    RETAIL_UI_ENDPOINTS_CARTS    = "http://${module.carts_service.ecs_service_name}"
    RETAIL_UI_ENDPOINTS_CHECKOUT = "http://${module.checkout_service.ecs_service_name}"
    RETAIL_UI_ENDPOINTS_ORDERS   = "http://${module.orders_service.ecs_service_name}"
  }
}
