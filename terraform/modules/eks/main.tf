# ─── EKS Cluster ─────────────────────────────────────────────────────────────
# Uses the official terraform-aws-modules/eks/aws community module (v20.x).
# This module handles:
#   - EKS control plane + cluster IAM role
#   - OIDC provider (required for IRSA)
#   - Managed node group IAM roles with ECR read + EKS worker policies
#   - Default security group rules for node-to-node and cluster-to-node traffic

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id                   = var.vpc_id
  subnet_ids               = var.private_subnet_ids
  control_plane_subnet_ids = var.private_subnet_ids

  cluster_endpoint_public_access = true

  enable_irsa = true

  # ── Core EKS managed add-ons ───────────────────────────────────────────────
  # vpc-cni  : Pod networking (must be before compute so nodes get ENIs before pods)
  # coredns  : In-cluster DNS
  # kube-proxy: Maintains iptables rules for Service routing
  cluster_addons = {
    vpc-cni = {
      most_recent    = true
      before_compute = true
      configuration_values = jsonencode({
        env = {
          # Required for security-group-for-pods (per-pod ENI)
          ENABLE_POD_ENI                    = "true"
          POD_SECURITY_GROUP_ENFORCING_MODE = "standard"
        }
      })
    }
    coredns = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
  }

  # Managed node groups (defined in node_groups.tf)
  eks_managed_node_groups = local.eks_managed_node_groups

  # Additional security group rules on the shared node SG 
  node_security_group_additional_rules = {
    ingress_self_all = {
      description = "Node to node – all ports/protocols"
      protocol    = "-1"
      from_port   = 0
      to_port     = 0
      type        = "ingress"
      self        = true
    }

    egress_all = {
      description      = "Node egress – all traffic"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      type             = "egress"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }
  }

  tags = var.tags
}
