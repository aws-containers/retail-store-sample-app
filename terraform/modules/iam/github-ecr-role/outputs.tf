output "role_arn" {
  description = "IAM role ARN used by GitHub Actions"
  value       = aws_iam_role.github_ecr.arn
}