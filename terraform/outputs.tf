################################################################################
# Outputs
################################################################################

output "cluster_name" {
  description = "The name of the EKS cluster"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "The endpoint for your EKS Kubernetes API"
  value       = module.eks.cluster_endpoint
}

output "region" {
  description = "AWS region"
  value       = var.region
}

output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "assets_bucket_name" {
  description = "The name of the S3 bucket for assets"
  value       = aws_s3_bucket.assets.id
}

output "bedrock_dev_view_access_key" {
  description = "Access Key ID for the bedrock-dev-view user"
  value       = aws_iam_access_key.dev_view_key.id
}

output "bedrock_dev_view_secret_key" {
  description = "Secret Access Key for the bedrock-dev-view user"
  value       = aws_iam_access_key.dev_view_key.secret
  sensitive   = true
}

output "lambda_function_name" {
  description = "Name of the asset processor Lambda function"
  value       = aws_lambda_function.processor.function_name
}