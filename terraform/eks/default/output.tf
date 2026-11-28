output "configure_kubectl" {
  description = "Command to update kubeconfig for this cluster"
  value       = module.retail_app_eks.configure_kubectl
}

output "retail_app_url" {
  description = "How to find the retail store URL (app is deployed by ArgoCD/the pipeline, not Terraform)"
  value       = "App deployed via ArgoCD. Get the UI endpoint with: kubectl get svc -n ui ui"
}
