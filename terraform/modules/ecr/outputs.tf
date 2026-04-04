output "repository_urls" {
  description = "Map of service name → ECR repository URL (used in CI/CD docker push commands)"
  value       = { for k, v in aws_ecr_repository.this : k => v.repository_url }
}

output "repository_arns" {
  description = "Map of service name → ECR repository ARN (used in IAM policies)"
  value       = { for k, v in aws_ecr_repository.this : k => v.arn }
}

output "registry_id" {
  description = "AWS account ID of the ECR registry (same as the AWS account)"
  value       = length(aws_ecr_repository.this) > 0 ? values(aws_ecr_repository.this)[0].registry_id : null
}
