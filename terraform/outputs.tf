# ─── Cluster ──────────────────────────────────────────────────────────────────
output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS API server endpoint"
  value       = module.eks.cluster_endpoint
}

output "configure_kubectl" {
  description = "Run locally to configure kubectl"
  value       = module.eks.configure_kubectl
}

# ─── Networking ───────────────────────────────────────────────────────────────
output "vpc_id" {
  description = "ID of the VPC"
  value       = module.vpc.output_vpc_id
}

output "private_subnet_ids" {
  description = "Private subnet IDs (worker nodes)"
  value       = module.vpc.output_private_subnets
}

output "public_subnet_ids" {
  description = "Public subnet IDs (load balancers)"
  value       = module.vpc.output_public_subnets
}

# ─── ECR ──────────────────────────────────────────────────────────────────────
output "ecr_repository_urls" {
  description = "Map of service name → ECR repository URL for docker push/pull"
  value       = module.ecr.repository_urls
}

output "ecr_registry_id" {
  description = "AWS account ID of the ECR registry"
  value       = module.ecr.registry_id
}

# ─── IRSA ─────────────────────────────────────────────────────────────────────
output "aws_lbc_role_arn" {
  description = "IAM role ARN for AWS Load Balancer Controller"
  value       = module.eks.aws_lbc_role_arn
}

output "cluster_autoscaler_role_arn" {
  description = "IAM role ARN for Cluster Autoscaler"
  value       = module.eks.cluster_autoscaler_role_arn
}

output "root_oidc_iam_role_arn" {
  description = "IAM role ARN for the optional root-level OIDC IAM role"
  value       = try(module.root_oidc_iam_role[0].iam_role_arn, null)
}

output "root_oidc_iam_role_name" {
  description = "IAM role name for the optional root-level OIDC IAM role"
  value       = try(module.root_oidc_iam_role[0].iam_role_name, null)
}
