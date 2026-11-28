data "aws_eks_cluster_auth" "this" {
  name = module.retail_app_eks.eks_cluster_id

  depends_on = [
    null_resource.cluster_blocker
  ]
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.retail_app_eks.eks_cluster_id
}

# data.kubernetes_service.ui_service removed — the "ui" Service is created by
# the application deployment, which is now owned by ArgoCD/the pipeline rather
# than Terraform. Discover the UI endpoint at runtime with:
#   kubectl get svc -n ui ui