module "checkout-elasticache-redis" {
  source  = "cloudposse/elasticache-redis/aws"
  version = "0.48.0"

  name              = "${var.environment_name}-elasticache-checkout"
  vpc_id            = module.vpc.inner.vpc_id
  instance_type     = "cache.t3.micro"
  subnets          = module.vpc.inner.private_subnets
  tags             = module.tags.result
}

module "vpc" {
  source = "../vpc"

  environment_name = var.environment_name
  tags             = module.tags.result
}

module "tags" {
  source = "../tags"

  environment_name = var.environment_name
}