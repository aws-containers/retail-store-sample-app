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

resource "aws_security_group_rule" "istio" {
  count = var.istio_enabled ? 1 : 0

  type              = "ingress"
  from_port         = 15012
  to_port           = 15012
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.node_security_group_id
}

resource "aws_security_group_rule" "istio_webhook" {
  count = var.istio_enabled ? 1 : 0

  type              = "ingress"
  from_port         = 15017
  to_port           = 15017
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.node_security_group_id
}

module "eks_blueprints_addons" {
  source  = "aws-ia/eks-blueprints-addons/aws"
  version = "~> 1.0"

  cluster_name      = module.eks_cluster.cluster_name
  cluster_endpoint  = module.eks_cluster.cluster_endpoint
  cluster_version   = module.eks_cluster.cluster_version
  oidc_provider_arn = module.eks_cluster.oidc_provider_arn

  enable_aws_load_balancer_controller = true
  enable_cert_manager                 = true
}

resource "time_sleep" "addons" {
  create_duration  = "30s"
  destroy_duration = "30s"

  depends_on = [
    module.eks_blueprints_addons
  ]
}

resource "null_resource" "cluster_blocker" {
  depends_on = [
    module.eks_cluster
  ]
}

resource "null_resource" "addons_blocker" {
  depends_on = [
    time_sleep.addons,
    aws_eks_addon.adot
  ]
}
