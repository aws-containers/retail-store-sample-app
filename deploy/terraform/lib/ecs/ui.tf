module "ui_service" {
  source = "./service"

  environment_name                = var.environment_name
  service_name                    = "ui"
  cluster_arn                     = aws_ecs_cluster.cluster.arn
  vpc_id                          = var.vpc_id
  vpc_cidr                        = var.vpc_cidr
  subnet_ids                      = var.subnet_ids
  public_subnet_ids               = var.public_subnet_ids
  tags                            = var.tags
  container_image                 = module.container_images.result.ui.url
  service_discovery_namespace_arn = aws_service_discovery_private_dns_namespace.this.arn
  cloudwatch_logs_group_id        = aws_cloudwatch_log_group.ecs_tasks.id
  healthcheck_path                = "/actuator/health"
  alb_target_group_arn            = element(module.alb.target_group_arns, 0)

  environment_variables = {
    ENDPOINTS_CATALOG  = "http://${module.catalog_service.ecs_service_name}"
    ENDPOINTS_CARTS    = "http://${module.carts_service.ecs_service_name}"
    ENDPOINTS_CHECKOUT = "http://${module.checkout_service.ecs_service_name}"
    ENDPOINTS_ORDERS   = "http://${module.orders_service.ecs_service_name}"
    ENDPOINTS_ASSETS   = "http://${module.assets_service.ecs_service_name}"
  }
}
