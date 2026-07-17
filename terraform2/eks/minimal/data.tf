data "aws_eks_cluster_auth" "this" {
  name = module.retail_app_eks.eks_cluster_id

  depends_on = [
    null_resource.cluster_blocker
  ]
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.retail_app_eks.eks_cluster_id
}

resource "null_resource" "cluster_blocker" {
  triggers = {
    "blocker" = module.retail_app_eks.cluster_blocker_id
  }
}