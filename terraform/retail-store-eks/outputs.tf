output "cluster_name" {
  value       = aws_eks_cluster.retail_store.name
  description = "EKS cluster name"
}

output "cluster_endpoint" {
  value       = aws_eks_cluster.retail_store.endpoint
  description = "EKS cluster API endpoint"
}

output "kubeconfig_command" {
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.retail_store.name}"
  description = "Run this to point kubectl at the cluster"
}

output "ecr_registry" {
  value       = local.ecr_registry
  description = "Private ECR registry base URL"
}

output "ecr_repositories" {
  value = {
    for k, v in aws_ecr_repository.services : k => v.repository_url
  }
  description = "Map of service name → ECR repository URL"
}

output "oidc_provider_arn" {
  value       = aws_iam_openid_connect_provider.eks.arn
  description = "OIDC provider ARN for IRSA"
}

output "alb_controller_role_arn" {
  value       = aws_iam_role.alb_controller.arn
  description = "IAM role ARN used by aws-load-balancer-controller"
}

output "github_actions_role_arn" {
  value       = aws_iam_role.github_actions.arn
  description = "IAM role ARN assumed by GitHub Actions via OIDC"
}

output "node_instance_role_arn" {
  value       = aws_iam_role.node_instance_role.arn
  description = "IAM role ARN of the worker node instance role"
}

output "ui_alb_dns_command" {
  value       = "kubectl get ingress ui -n default -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
  description = "Run after apply to get the ALB hostname for the UI"
}
