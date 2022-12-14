module "orders_mq_broker" {
    source = "cloudposse/mq-broker/aws"

    name                       = "${var.environment_name}_orders_mq-broker"
    vpc_id                     = module.vpc.inner.vpc_id
    subnet_ids                 = [module.vpc.inner.private_subnets[0]]
    mq_admin_user              = ["orders_mq_user"]
    mq_admin_password          = [random_password.mq_password.result]
    deployment_mode            = "SINGLE_INSTANCE"

    tags             = module.tags.result

}

resource "random_password" "mq_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_+{}<>?"
}

module "mq_vpc" {
  source = "../vpc"

  environment_name = var.environment_name
  tags             = module.tags.result
}

module "mq_tags" {
  source = "../tags"

  environment_name = var.environment_name
}