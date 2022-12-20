#---------------------------------------------------------------
# EKS Cluster
#---------------------------------------------------------------

module "eks_cluster" {
  source = "github.com/aws-ia/terraform-aws-eks-blueprints?ref=v4.19.0"

  cluster_name    = var.environment_name
  cluster_version = var.cluster_version

  vpc_id             = var.vpc_id
  private_subnet_ids = var.subnet_ids

  #----------------------------------------------------------------------------------------------------------#
  # Security groups used in this module created by the upstream modules terraform-aws-eks (https://github.com/terraform-aws-modules/terraform-aws-eks).
  #   Upstream module implemented Security groups based on the best practices doc https://docs.aws.amazon.com/eks/latest/userguide/sec-group-reqs.html.
  #   So, by default the security groups are restrictive. Users needs to enable rules for specific ports required for App requirement or Add-ons
  #   See the notes below for each rule used in these examples
  #----------------------------------------------------------------------------------------------------------#
  node_security_group_additional_rules = {
    # Extend node-to-node security group rules. Recommended and required for the Add-ons
    ingress_self_all = {
      description = "Node to node all ports/protocols"
      protocol    = "-1"
      from_port   = 0
      to_port     = 0
      type        = "ingress"
      self        = true
    }
    # Recommended outbound traffic for Node groups
    egress_all = {
      description      = "Node all egress"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      type             = "egress"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }
    # Allows Control Plane Nodes to talk to Worker nodes on all ports. Added this to simplify the example and further avoid issues with Add-ons communication with Control plane.
    # This can be restricted further to specific port based on the requirement for each Add-on e.g., metrics-server 4443, spark-operator 8080, karpenter 8443 etc.
    # Change this according to your security requirements if needed
    ingress_cluster_to_node_all_traffic = {
      description                   = "Cluster API to Nodegroup all traffic"
      protocol                      = "-1"
      from_port                     = 0
      to_port                       = 0
      type                          = "ingress"
      source_cluster_security_group = true
    }
  }

  managed_node_groups = {
    mg_5 = {
      node_group_name      = "managed-ondemand"
      instance_types       = ["m5.large"]
      subnet_ids           = var.subnet_ids
      force_update_version = true
    }
  }

  tags = var.tags
}

module "eks_cluster_kubernetes_addons" {
  source = "github.com/aws-ia/terraform-aws-eks-blueprints//modules/kubernetes-addons?ref=v4.19.0"

  eks_cluster_id               = module.eks_cluster.eks_cluster_id
  eks_cluster_endpoint         = module.eks_cluster.eks_cluster_endpoint
  eks_oidc_provider            = module.eks_cluster.oidc_provider
  eks_cluster_version          = module.eks_cluster.eks_cluster_version
  eks_worker_security_group_id = module.eks_cluster.worker_node_security_group_id
  auto_scaling_group_names     = module.eks_cluster.self_managed_node_group_autoscaling_groups

  # EKS Addons
  enable_amazon_eks_vpc_cni = true
  amazon_eks_vpc_cni_config = {
    most_recent = true
  }

  enable_amazon_eks_coredns = true
  amazon_eks_coredns_config = {
    most_recent = true
  }

  enable_amazon_eks_kube_proxy         = true
  enable_amazon_eks_aws_ebs_csi_driver = true

  enable_prometheus                    = true
  enable_amazon_prometheus             = true
  amazon_prometheus_workspace_endpoint = module.managed_prometheus.workspace_prometheus_endpoint

  enable_aws_for_fluentbit                 = true
  aws_for_fluentbit_create_cw_log_group    = false
  aws_for_fluentbit_cw_log_group_retention = 30
  aws_for_fluentbit_helm_config = {
    create_namespace = true
  }

  enable_kyverno                 = true
  enable_kyverno_policies        = true
  enable_kyverno_policy_reporter = true

  tags = var.tags

  depends_on = [
    module.eks_cluster,
    module.managed_prometheus
  ]
}

#---------------------------------------------------------------
# Supporting Resources
#---------------------------------------------------------------

module "managed_prometheus" {
  source  = "terraform-aws-modules/managed-service-prometheus/aws"
  version = "~> 2.1"

  workspace_alias = var.environment_name

  tags = var.tags
}
