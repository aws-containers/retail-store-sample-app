variable "project_name" {
  type = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string

  validation {
    condition = contains(
      ["dev", "staging", "prod"],
      var.environment
    )

    error_message = "Environment must be dev, staging, or prod."
  }
}

variable "github_repo" {
  type = string
}

variable "github_branch" {
  type = string
}

variable "oidc_provider_arn" {
  type = string
}

variable "ecr_repository_arns" {
  type = set(string)
  validation {
    condition     = length(var.ecr_repository_arns) > 0
    error_message = "At least one ECR repository ARN must be provided."
  }
}