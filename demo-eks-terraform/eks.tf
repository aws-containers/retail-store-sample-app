
module "use_eksClusterRole" {
  count  = var.use_predefined_role ? 1 : 0
  source = "./modules/use-service-role"

  cluster_role_name = var.cluster_role_name
}

module "create_eksClusterRole" {
  count  = var.use_predefined_role ? 0 : 1
  source = "./modules/create-service-role"

  cluster_role_name = var.cluster_role_name
}

####################################################################
#
# Creates the EKS Cluster control plane
#
####################################################################

resource "aws_eks_cluster" "demo_eks" {
  name = var.cluster_name
  # Stable literal ARN — avoids (known after apply) during plan when the
  # role's module instance is being created, which would otherwise force
  # cluster replacement.
  role_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/${var.cluster_role_name}"

  depends_on = [
    module.create_eksClusterRole,
    module.use_eksClusterRole,
  ]

  vpc_config {
    subnet_ids = [
      data.aws_subnets.public.ids[0],
      data.aws_subnets.public.ids[1],
      data.aws_subnets.public.ids[2]
    ]
  }

  access_config {
    authentication_mode                         = "CONFIG_MAP"
    bootstrap_cluster_creator_admin_permissions = true
  }

  # describe-cluster returns accessConfig: null on EKS 1.35, which Terraform
  # reads as drift and tries to fix via UpdateClusterConfig — denied for this
  # lab's IAM user. The block above only matters at create time.
  lifecycle {
    ignore_changes = [access_config]
  }
}

