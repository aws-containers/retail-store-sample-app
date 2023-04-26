module "tags" {
  source = "../../lib/tags"

  environment_name = var.environment_name
}

module "vpc" {
  source = "../../lib/vpc"

  environment_name = var.environment_name

  public_subnet_tags = {
    "kubernetes.io/cluster/${var.environment_name}" = "shared"
    "kubernetes.io/role/elb"                        = 1
  }

  private_subnet_tags = {
    "kubernetes.io/cluster/${var.environment_name}" = "shared"
    "kubernetes.io/role/internal-elb"               = 1
  }

  tags = module.tags.result
}

module "dependencies" {
  source = "../../lib/dependencies"

  environment_name = var.environment_name
  tags             = module.tags.result

  vpc_id             = module.vpc.inner.vpc_id
  subnet_ids         = module.vpc.inner.private_subnets
  availability_zones = module.vpc.inner.azs

  catalog_security_group_id  = var.tracing_enabled ? module.retail_app_eks.node_security_group_id : aws_security_group.catalog.id
  orders_security_group_id   = var.tracing_enabled ? module.retail_app_eks.node_security_group_id : aws_security_group.orders.id
  checkout_security_group_id = var.tracing_enabled ? module.retail_app_eks.node_security_group_id : aws_security_group.checkout.id
}

module "retail_app_eks" {
  source = "../../lib/eks"

  providers = {
    kubernetes.cluster = kubernetes.cluster
    kubernetes.addons = kubernetes

    helm = helm
  }

  environment_name = var.environment_name
  cluster_version  = "1.24"
  vpc_id           = module.vpc.inner.vpc_id
  vpc_cidr         = module.vpc.inner.vpc_cidr_block
  subnet_ids       = module.vpc.inner.private_subnets
  tags             = module.tags.result

  istio_enabled = var.istio_enabled
}