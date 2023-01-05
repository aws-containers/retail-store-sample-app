output "eks_cluster_id" {
  description = "Amazon EKS Cluster Name"
  value       = module.eks_cluster.eks_cluster_id
}

output "configure_kubectl" {
  description = "Configure kubectl: make sure you're logged in with the correct AWS profile and run the following command to update your kubeconfig"
  value       = module.eks_cluster.configure_kubectl
}

output "cluster_endpoint" {
  description = "Cluster end point"
  value       = module.eks_cluster.eks_cluster_endpoint
}

output "cluster_object" {
  description = "EKS cluster object map"
  value       = module.eks_cluster
}

output "eks_cluster_kubernetes_addons" {
  description = "Map of attributes of the EKS Blueprints Kubernetes addons Helm release and IRSA created"
  value       = module.eks_cluster_kubernetes_addons
}

output "kyverno_addon" {
  description = "Map of attributes of the Helm release and IRSA created"
  value       = module.eks_cluster_kubernetes_addons.kyverno
}

output "kyverno_values" {
  description = "Values used in the Kyverno Helm release"
  value       = try(jsondecode(module.eks_cluster_kubernetes_addons.kyverno.release_metadata[0].values), null)
}
