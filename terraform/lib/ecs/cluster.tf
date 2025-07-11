resource "aws_ecs_cluster" "cluster" {
  name = "${var.environment_name}-cluster"

  setting {
    name  = "containerInsights"
    value = var.container_insights_setting
  }
}

resource "aws_cloudwatch_log_group" "ecs_tasks" {
  name = "${var.environment_name}-tasks"
}

resource "aws_service_discovery_private_dns_namespace" "this" {
  name        = "retailstore.local"
  description = "Service discovery namespace"
  vpc         = var.vpc_id
}
