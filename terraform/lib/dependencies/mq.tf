module "orders_mq_broker" {
    source = "cloudposse/mq-broker/aws"

    name                       = "${var.environment_name}_orders_mq-broker"
    vpc_id                     = var.vpc_id
    subnet_ids                 = [var.subnet_ids[0]]
    mq_admin_user              = ["orders_mq_user"]
    mq_admin_password          = [random_password.mq_password.result]
    deployment_mode            = "SINGLE_INSTANCE"

    tags             = var.tags

}

resource "random_password" "mq_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_+{}<>?"
}