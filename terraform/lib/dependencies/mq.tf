locals {
  mq_default_user = "default_mq_user"
}

module "orders_mq_broker" {
  source = "cloudposse/mq-broker/aws"

  name                       = "${var.environment_name}_orders_mq-broker"
  vpc_id                     = var.vpc_id
  subnet_ids                 = [var.subnet_ids[0]]
  mq_admin_user              = [local.mq_default_user]
  mq_admin_password          = [random_password.mq_password.result]
  deployment_mode            = "SINGLE_INSTANCE"
  engine_type                = "RabbitMQ"
  engine_version             = "3.10.10"
  audit_log_enabled          = false
  encryption_enabled         = false

  allowed_security_group_ids = [var.orders_security_group_id]

  tags             = var.tags
}

resource "random_password" "mq_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_+{}<>?"
}