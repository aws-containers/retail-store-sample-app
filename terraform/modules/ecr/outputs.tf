output "repository_urls" {
  description = "URLs of the created ECR repositories"

  value = {
    for name, repository in aws_ecr_repository.this :
    name => repository.repository_url
  }
}

output "repository_arns" {
  description = "ARNs of the created ECR repositories"

  value = {
    for name, repository in aws_ecr_repository.this :
    name => repository.arn
  }
}

output "repository_names" {
  description = "Names of the created ECR repositories"

  value = {
    for name, repository in aws_ecr_repository.this :
    name => repository.name
  }
}

output "registry_id" {
  description = "AWS ECR registry ID"

  value = try(
    values(aws_ecr_repository.this)[0].registry_id,
    null
  )
}