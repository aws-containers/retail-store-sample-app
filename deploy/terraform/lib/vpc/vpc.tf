locals {
  private_subnets = [for k, v in local.azs : cidrsubnet(var.vpc_cidr, 8, k + 10)]
  public_subnets  = [for k, v in local.azs : cidrsubnet(var.vpc_cidr, 8, k)]
  azs             = slice(data.aws_availability_zones.available.names, 0, 3)
}

data "aws_availability_zones" "available" {
  state = "available"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "3.18.1"

  name = var.environment_name
  cidr = var.vpc_cidr

  azs             = local.azs
  public_subnets  = local.public_subnets
  private_subnets = local.private_subnets

  enable_nat_gateway   = true
  create_igw           = true
  enable_dns_hostnames = true
  single_nat_gateway   = true

  # Manage so we can name
  manage_default_network_acl    = true
  default_network_acl_tags      = { Name = "${var.environment_name}-default" }
  manage_default_route_table    = true
  default_route_table_tags      = { Name = "${var.environment_name}-default" }
  manage_default_security_group = true
  default_security_group_tags   = { Name = "${var.environment_name}-default" }

  public_subnet_tags  = merge(var.tags, var.public_subnet_tags)
  private_subnet_tags = merge(var.tags, var.private_subnet_tags)

  tags = var.tags
}
