data "aws_eks_cluster_auth" "this" {
  name = module.eks_cluster.cluster_name
}

data "aws_availability_zones" "available" {}

data "aws_region" "current" {}

data "aws_partition" "current" {}
