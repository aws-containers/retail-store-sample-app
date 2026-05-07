output "argocd_namespace" {
  description = "Namespace where ArgoCD is deployed"
  value       = var.argocd_namespace
}

output "argocd_helm_release_name" {
  description = "Helm release name for ArgoCD"
  value       = helm_release.argocd.name
}

output "argocd_repo_secret_name" {
  description = "Kubernetes secret name containing repo credentials"
  value       = kubernetes_secret.gitops_repository.metadata[0].name
}
