variable "aws_region" {
  type        = string
  description = "The AWS region to deploy resources in"

}

variable "project_name" {
  type        = string
  description = "The name of the project"
}

variable "env" {
  type        = string
  description = "The environment name"
}

variable "vpc_cidr" {
  type        = string
  description = "The CIDR block for the VPC"
  validation {
    condition     = can(cidrhost(var.vpc_cidr, 0))
    error_message = "vpc_cidr must be a valid IPv4 CIDR block."
  }
}

variable "public_subnets" {
  type = map(object({
    cidr_block        = string
    availability_zone = string
  }))
}

variable "private_subnets" {
  type = map(object({
    cidr_block        = string
    availability_zone = string
  }))
}


variable "ecr_repository_names" {
  description = "ECR repositories required by the retail application"
  type        = set(string)
}

variable "github_repository" {
  type = string
}

variable "github_oidc_provider_arn" {
  type = string
}

variable "owner" {
  type        = string
  description = "The owner of the resources"
}