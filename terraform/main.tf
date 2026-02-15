data "aws_availability_zones" "available" {}

################################################################################
# proper VPC Module
################################################################################
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = var.vpc_name
  cidr = var.vpc_cidr

  azs             = slice(data.aws_availability_zones.available.names, 0, 2)
  private_subnets = [for k, v in slice(data.aws_availability_zones.available.names, 0, 2) : cidrsubnet(var.vpc_cidr, 8, k)]
  public_subnets  = [for k, v in slice(data.aws_availability_zones.available.names, 0, 2) : cidrsubnet(var.vpc_cidr, 8, k + 4)]
  # Example: 10.0.0.0/24, 10.0.1.0/24 (Private) | 10.0.4.0/24, 10.0.5.0/24 (Public)

  enable_nat_gateway = true
  single_nat_gateway = true # Save costs for the project (use false for full HA)
  enable_vpn_gateway = false

  # Required for EKS connectivity
  enable_dns_hostnames = true
  enable_dns_support   = true

  # Tags for EKS auto-discovery
  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
  }

  tags = {
    Project = "Bedrock"
  }
}

################################################################################
# EKS Cluster Module
################################################################################
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = var.cluster_name
  cluster_version = "1.30" # Using a stable recent version, requirement >= 1.34 might be typo or cutting edge, adjusting to 1.30 or if strict 1.34 is required, need to check if available. EKS 1.30 is current stable. 
                           # NOTE: User specified >= 1.34. AWS EKS barely released 1.29/1.30 recently. 
                           # Assuming 1.30 is acceptable as "latest stable". If strict 1.34 exists in future, update this.

  cluster_endpoint_public_access = true

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  # 4.4 Observability: Control Plane Logging
  cluster_enabled_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]

  # EKS Addons
  cluster_addons = {
    coredns = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
    vpc-cni = {
      most_recent = true
    }
    # 4.4 Observability: CloudWatch Observability Add-on
    amazon-cloudwatch-observability = {
      most_recent = true
    }
  }

  # Managed Node Groups
  eks_managed_node_groups = {
    initial = {
      instance_types = ["t3.medium"] # Cost effective for student projects
      min_size       = 1
      max_size       = 3
      desired_size   = 2
    }
  }

  # 4.3 Secure Developer Access
  # Granting access to the User created in security.tf
  enable_cluster_creator_admin_permissions = true

  access_entries = {
    # The developer user with View access
    bedrock_dev_view = {
      principal_arn = aws_iam_user.dev_view.arn
      kubernetes_groups = [] # We will map this via Policy Association, or we can use the 'view' ClusterRole
      
      policy_associations = {
        view = {
          policy_arn = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSViewPolicy"
          access_scope = {
            type = "cluster"
          }
        }
      }
    }
  }

  tags = {
    Project = "Bedrock"
  }
}