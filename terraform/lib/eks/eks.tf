module "eks_cluster" {
  source = "github.com/aws-ia/terraform-aws-eks-blueprints?ref=v4.20.0"

  cluster_name    = var.environment_name
  cluster_version = var.cluster_version

  vpc_id             = var.vpc_id
  private_subnet_ids = var.subnet_ids

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

  managed_node_groups = {
    node_group_1 = {
      node_group_name      = "managed-nodegroup-1"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[0]]
      desired_size         = 1
      force_update_version = true
    }
    node_group_2 = {
      node_group_name      = "managed-nodegroup-2"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[1]]
      desired_size         = 1
      force_update_version = true
    }
    node_group_3 = {
      node_group_name      = "managed-nodegroup-3"
      instance_types       = ["m5.large"]
      subnet_ids           = [var.subnet_ids[2]]
      desired_size         = 1
      force_update_version = true
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
  security_group_id = module.eks_cluster.cluster_primary_security_group_id
}

resource "aws_security_group_rule" "dns_tcp" {
  type              = "ingress"
  from_port         = 53
  to_port           = 53
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = module.eks_cluster.cluster_primary_security_group_id
}

module "eks_cluster_kubernetes_addons" {
  source = "github.com/aws-ia/terraform-aws-eks-blueprints//modules/kubernetes-addons?ref=v4.20.0"

  eks_cluster_id               = module.eks_cluster.eks_cluster_id
  eks_cluster_endpoint         = module.eks_cluster.eks_cluster_endpoint
  eks_oidc_provider            = module.eks_cluster.oidc_provider
  eks_cluster_version          = module.eks_cluster.eks_cluster_version

  enable_aws_for_fluentbit                 = true
  aws_for_fluentbit_create_cw_log_group    = false
  aws_for_fluentbit_cw_log_group_retention = 30
  aws_for_fluentbit_helm_config = {
    create_namespace = true
  }

  tags = var.tags
}

locals {
  kubeconfig = yamlencode({
    apiVersion      = "v1"
    kind            = "Config"
    current-context = "terraform"
    clusters = [{
      name = module.eks_cluster.eks_cluster_id
      cluster = {
        certificate-authority-data = module.eks_cluster.eks_cluster_certificate_authority_data
        server                     = module.eks_cluster.eks_cluster_endpoint
      }
    }]
    contexts = [{
      name = "terraform"
      context = {
        cluster = module.eks_cluster.eks_cluster_id
        user    = "terraform"
      }
    }]
    users = [{
      name = "terraform"
      user = {
        token = data.aws_eks_cluster_auth.this.token
      }
    }]
  })
}

resource "null_resource" "kubectl_set_env" {
  triggers = {
    cluster_arns = module.eks_cluster.eks_cluster_arn
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    environment = {
      KUBECONFIG = base64encode(local.kubeconfig)
    }

    command = <<-EOT
      sleep 30
      kubectl set env daemonset aws-node -n kube-system POD_SECURITY_GROUP_ENFORCING_MODE=standard --kubeconfig <(echo $KUBECONFIG | base64 --decode)
      sleep 10
    EOT
  }
}

data "aws_eks_addon_version" "latest" {
  for_each = toset(["vpc-cni"])

  addon_name         = each.value
  kubernetes_version = var.cluster_version
  most_recent        = true
}

resource "aws_eks_addon" "vpc_cni" {
  cluster_name         = module.eks_cluster.eks_cluster_id
  addon_name           = "vpc-cni"
  addon_version        = data.aws_eks_addon_version.latest["vpc-cni"].version
  resolve_conflicts    = "OVERWRITE"
  configuration_values = "{\"env\":{\"ENABLE_POD_ENI\":\"true\"}}"

  tags = var.tags

  depends_on = [
    null_resource.kubectl_set_env
  ]
}