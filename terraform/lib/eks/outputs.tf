output "eks_cluster_id" {
  description = "Amazon EKS Cluster Name"
  value       = module.eks_cluster.cluster_name
}

output "cluster_endpoint" {
  description = "Cluster end point"
  value       = module.eks_cluster.cluster_endpoint
}

output "cluster_certificate_authority_data" {
  description = "Cluster CA data"
  value       = module.eks_cluster.cluster_certificate_authority_data
}

output "eks_oidc_issuer_url" {
  description = "EKS cluster OIDC issuer URL"
  value       = module.eks_cluster.cluster_oidc_issuer_url
}

output "node_security_group_id" {
  description = "ID of the node shared security group"
  value       = module.eks_cluster.node_security_group_id
}

output "cluster_blocker_id" {
  description = "Output that can be used to block other resources until cluster is created"
  value       = null_resource.cluster_blocker.id
}

output "addons_blocker_id" {
  description = "Output that can be used to block other resources until addons are created"
  value       = null_resource.addons_blocker.id
}

output "configure_kubectl" {
  description = "Command to update kubeconfig for this cluster"
  value       = "aws eks --region ${data.aws_region.current.name} update-kubeconfig --name ${module.eks_cluster.cluster_name}"
}

output "adot_namespace" {
  description = "Namespace where ADOT is deployed"
  value       = kubernetes_namespace_v1.adot.metadata[0].name
}
