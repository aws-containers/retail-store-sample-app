####################################################################
# EKS Cluster control plane
####################################################################

module "create_cluster_role" {
  source = "./modules/create-service-role"

  cluster_role_name = var.cluster_role_name
  additional_policy_arns = [
    aws_iam_policy.node_additional.arn,
  ]
}

resource "aws_eks_cluster" "retail_store" {
  name     = var.cluster_name
  role_arn = module.create_cluster_role.eksClusterRole_arn
  version  = "1.31"

  vpc_config {
    subnet_ids = [
      data.aws_subnets.public.ids[0],
      data.aws_subnets.public.ids[1],
      data.aws_subnets.public.ids[2],
    ]
  }

  access_config {
    authentication_mode                         = "CONFIG_MAP"
    bootstrap_cluster_creator_admin_permissions = true
  }

  depends_on = [module.create_cluster_role]
}
