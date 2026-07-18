variable "project_name" {
  type = string
}

variable "environment" {
  type = string
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
}