output "iam_role_arn" {
  description = "ARN of the IAM role used by the Kubernetes service account."
  value       = module.this.iam_role_arn
}

output "iam_role_name" {
  description = "Name of the IAM role used by the Kubernetes service account."
  value       = module.this.iam_role_name
}
