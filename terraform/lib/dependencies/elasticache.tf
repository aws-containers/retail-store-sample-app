module "checkout-elasticache-redis" {
  source  = "cloudposse/elasticache-redis/aws"
  version = "0.49.0"

  name                       = "${var.environment_name}-checkout"
  vpc_id                     = var.vpc_id
  instance_type              = "cache.t3.micro"
  subnets                    = var.subnet_ids
  transit_encryption_enabled = false
  tags                       = var.tags

  allowed_security_group_ids = [var.checkout_security_group_id]
}