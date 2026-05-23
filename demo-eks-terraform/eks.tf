
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
  name     = var.cluster_name
  role_arn = var.use_predefined_role ? module.use_eksClusterRole[0].eksClusterRole_arn : module.create_eksClusterRole[0].eksClusterRole_arn

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
}

