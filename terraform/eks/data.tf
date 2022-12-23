data "aws_region" "current" {}

data "aws_eks_cluster_auth" "this" {
  name = module.retail_app_eks.eks_cluster_id
}
