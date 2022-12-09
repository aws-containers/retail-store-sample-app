locals {
  private_subnets        = [for k, v in local.azs : cidrsubnet(var.vpc_cidr, 8, k + 10)]
  public_subnets         = [for k, v in local.azs : cidrsubnet(var.vpc_cidr, 8, k)]
  azs                    = slice(data.aws_availability_zones.available.names, 0, 3)
}

data "aws_availability_zones" "available" {
  state = "available"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "3.18.1"

  name                  = var.environment_name
  cidr                  = var.vpc_cidr
  azs                   = local.azs

  public_subnets  = local.public_subnets
  private_subnets = local.private_subnets

  enable_nat_gateway   = true
  create_igw           = true
  enable_dns_hostnames = true
  single_nat_gateway   = true

  public_subnet_tags  = merge(var.tags, var.public_subnet_tags)
  private_subnet_tags = merge(var.tags, var.private_subnet_tags)

  tags = var.tags
}