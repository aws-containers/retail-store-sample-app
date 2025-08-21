output "configure_kubectl" {
  description = "Command to update kubeconfig for this cluster"
  value       = module.retail_app_eks.configure_kubectl
}

output "retail_app_url" {
  description = "URL to access the retail store application"
  value = try(
    "http://${data.kubernetes_service.ui_service.status[0].load_balancer[0].ingress[0].hostname}",
    "LoadBalancer provisioning - run: kubectl get svc -n ui ui"
  )
}
