module "eks_cluster" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.9"

  providers = {
    kubernetes = kubernetes.cluster
  }

  cluster_name                   = var.environment_name
  cluster_version                = var.cluster_version
  cluster_endpoint_public_access = true

  cluster_addons = {
    vpc-cni = {
      before_compute = true
      most_recent    = true
      configuration_values = jsonencode({
        env = {
          ENABLE_POD_ENI                    = "true"
          POD_SECURITY_GROUP_ENFORCING_MODE = "standard"
        }
      })
    }
  }

  vpc_id = var.vpc_id

  subnet_ids               = var.subnet_ids
  control_plane_subnet_ids = var.subnet_ids

  eks_managed_node_groups = {
    node_group_1 = {
      name                 = "managed-nodegroup-1"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[0]]
      force_update_version = true

      min_size     = 1
      max_size     = 3
      desired_size = 1
    }

    node_group_2 = {
      name                 = "managed-nodegroup-2"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[1]]
      force_update_version = true

      min_size     = 1
      max_size     = 3
      desired_size = 1
    }

    node_group_3 = {
      name                 = "managed-nodegroup-3"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[2]]
      force_update_version = true

      min_size     = 1
      max_size     = 3
      desired_size = 1
    }
  }

  node_security_group_additional_rules = {
    ingress_self_all = {
      description = "Node to node all ports/protocols"
      protocol    = "-1"
      from_port   = 0
      to_port     = 0
      type        = "ingress"
      self        = true
    }

    egress_all = {
      description      = "Node all egress"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      type             = "egress"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }

    ingress_cluster_to_node_all_traffic = {
      description                   = "Cluster API to Nodegroup all traffic"
      protocol                      = "-1"
      from_port                     = 0
      to_port                       = 0
      type                          = "ingress"
      source_cluster_security_group = true
    }
  }

  tags = var.tags
}

resource "aws_security_group_rule" "dns_udp" {
  type              = "ingress"
  from_port         = 53
  to_port           = 53
  protocol          = "udp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.node_security_group_id
}

resource "aws_security_group_rule" "dns_tcp" {
  type              = "ingress"
  from_port         = 53
  to_port           = 53
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.node_security_group_id
}

resource "aws_security_group_rule" "istio_citadel" {
  count = var.istio_enabled ? 1 : 0

  type              = "ingress"
  from_port         = 15012
  to_port           = 15012
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.node_security_group_id
}

module "eks_cluster_kubernetes_addons" {
  source = "github.com/aws-ia/terraform-aws-eks-blueprints//modules/kubernetes-addons?ref=v4.26.0"

  providers = {
    kubernetes = kubernetes.addons
    helm       = helm
  }

  depends_on = [
    module.eks_cluster
  ]

  eks_cluster_id               = module.eks_cluster.cluster_name
  eks_cluster_endpoint         = module.eks_cluster.cluster_endpoint
  eks_oidc_provider            = module.eks_cluster.oidc_provider
  eks_cluster_version          = module.eks_cluster.cluster_version

  enable_tetrate_istio = var.istio_enabled

  enable_aws_for_fluentbit                 = true
  aws_for_fluentbit_create_cw_log_group    = false
  aws_for_fluentbit_cw_log_group_retention = 30
  aws_for_fluentbit_helm_config = {
    create_namespace = true
  }

  enable_amazon_eks_adot                   =true
  amazon_eks_adot_config = {
    kubernetes_version = var.cluster_version
  }
  
  tags = var.tags
}

resource "null_resource" "cluster_blocker" {
  depends_on = [
    module.eks_cluster
  ]
}

resource "null_resource" "addons_blocker" {
  depends_on = [
    module.eks_cluster_kubernetes_addons
  ]
}