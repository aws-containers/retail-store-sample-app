module "checkout-elasticache-redis" {
  source  = "cloudposse/elasticache-redis/aws"
  version = "0.48.0"

  name              = "${var.environment_name}-elasticache-checkout"
  vpc_id            = var.vpc_id
  instance_type     = "cache.t3.micro"
  subnets          = var.subnet_ids
  tags             = var.tags
}