locals {
  cluster_name = "${var.project_name}-${var.environment}-eks"

  common_tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    },
    var.tags
  )
}

# ─── VPC ─────────────────────────────────────────────────────────────────────
# Two public subnets (Load Balancer facing) and two private subnets
# (worker nodes) spread across two AZs for high availability.
# A single NAT Gateway is used to keep costs low for non-production.

data "aws_availability_zones" "available" {
  state = "available"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "${var.project_name}-${var.environment}-vpc"
  cidr = var.vpc_cidr

  # Pick the first 2 available AZs dynamically
  azs = slice(data.aws_availability_zones.available.names, 0, 2)

  # /20 subnets give 4094 IPs each – plenty for pods and nodes
  private_subnets = [cidrsubnet(var.vpc_cidr, 4, 0), cidrsubnet(var.vpc_cidr, 4, 1)]
  public_subnets  = [cidrsubnet(var.vpc_cidr, 4, 2), cidrsubnet(var.vpc_cidr, 4, 3)]

  enable_nat_gateway   = true
  single_nat_gateway   = false # Switch to false in production for HA
  enable_dns_hostnames = true
  enable_dns_support   = true

  # Tags required by the AWS Load Balancer Controller to auto-discover subnets
  public_subnet_tags = {
    "kubernetes.io/role/elb"                      = "1"
    "kubernetes.io/cluster/${local.cluster_name}" = "shared"
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb"             = "1"
    "kubernetes.io/cluster/${local.cluster_name}" = "shared"
  }

  tags = local.common_tags
}

# ─── ECR ─────────────────────────────────────────────────────────────────────
# Private container registries for each microservice.
# Docker images built by CI/CD are pushed here; EKS nodes pull from here.

module "ecr" {
  source = "./modules/ecr"

  project_name = var.project_name
  services     = var.ecr_services
  tags         = local.common_tags
}

# ─── EKS ─────────────────────────────────────────────────────────────────────
# EKS cluster with worker nodes across 2 AZs, core add-ons,
# and IRSA roles for AWS Load Balancer Controller, EBS CSI, and Cluster Autoscaler.

module "eks" {
  source = "./modules/eks"

  cluster_name    = local.cluster_name
  cluster_version = var.eks_cluster_version

  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnets
  public_subnet_ids  = module.vpc.public_subnets

  node_instance_type = var.node_instance_type
  node_min_size      = var.node_min_size
  node_max_size      = var.node_max_size
  node_desired_size  = var.node_desired_size

  tags = local.common_tags
}

# Optional root-level OIDC IAM role (IRSA) for app workloads.
module "root_oidc_iam_role" {
  count  = var.create_root_oidc_iam_role ? 1 : 0
  source = "./modules/oidc_iam_role"

  role_name                  = var.root_oidc_iam_role_name
  oidc_provider_arn          = module.eks.oidc_provider_arn
  namespace_service_accounts = var.root_oidc_namespace_service_accounts

  tags = local.common_tags
}
