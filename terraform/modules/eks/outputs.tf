# Cluster identity
output "cluster_name" {
  description = "Name of the EKS cluster"
  value       = module.eks.cluster_name
}

output "cluster_version" {
  description = "Kubernetes version of the cluster"
  value       = module.eks.cluster_version
}

output "cluster_endpoint" {
  description = "API server endpoint URL (used to configure kubectl and providers)"
  value       = module.eks.cluster_endpoint
}

output "cluster_certificate_authority_data" {
  description = "Base64-encoded CA certificate for the cluster (used by kubectl/helm providers)"
  value       = module.eks.cluster_certificate_authority_data
}

output "oidc_provider_arn" {
  description = "ARN of the IAM OIDC identity provider (used for IRSA role trust policies)"
  value       = module.eks.oidc_provider_arn
}

output "cluster_oidc_issuer_url" {
  description = "OIDC issuer URL of the cluster"
  value       = module.eks.cluster_oidc_issuer_url
}

# Node groups
output "node_security_group_id" {
  description = "ID of the shared security group attached to all worker nodes"
  value       = module.eks.node_security_group_id
}

output "eks_managed_node_groups" {
  description = "Map of managed node group attributes keyed by group name"
  value       = module.eks.eks_managed_node_groups
}

# IRSA role ARNs (passed to helm_release in the root module)
output "aws_lbc_role_arn" {
  description = "IAM role ARN for the AWS Load Balancer Controller service account"
  value       = module.aws_lbc_irsa.iam_role_arn
}

output "ebs_csi_role_arn" {
  description = "IAM role ARN for the EBS CSI Driver service account"
  value       = module.ebs_csi_irsa.iam_role_arn
}

output "cluster_autoscaler_role_arn" {
  description = "IAM role ARN for the Cluster Autoscaler service account"
  value       = module.cluster_autoscaler_irsa.iam_role_arn
}

# Convenience
output "configure_kubectl" {
  description = "Run this command locally to configure kubectl for this cluster"
  value       = "aws eks update-kubeconfig --region $(aws configure get region) --name ${module.eks.cluster_name}"
}
