output "configure_kubectl" {
  description = "Configure kubectl: make sure you're logged in with the correct AWS profile and run the following command to update your kubeconfig"
  value       = module.retail_app_eks.configure_kubectl
}

output "cluster_endpoint" {
  description = "Cluster end point"
  value       = module.retail_app_eks.cluster_endpoint
}
