variable "aws_region" {
  description = "AWS region where all resources will be deployed"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Short name for this project - used as a prefix in resource names"
  type        = string
  default     = "ecommerce"
}

variable "environment" {
  description = "Deployment environment label (dev | staging | prod)"
  type        = string
  default     = "dev"
}

# Networking
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# EKS
variable "eks_cluster_version" {
  description = "Kubernetes version to deploy on EKS"
  type        = string
  default     = "1.34"
}

variable "node_instance_type" {
  description = "EC2 instance type for worker nodes"
  type        = string
  default     = "t3.medium"
}

variable "node_min_size" {
  description = "Minimum nodes per node group"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum nodes per node group (Cluster Autoscaler ceiling)"
  type        = number
  default     = 3
}

variable "node_desired_size" {
  description = "Initial desired node count per node group"
  type        = number
  default     = 2
}

# ECR
variable "ecr_services" {
  description = "Microservices that require a dedicated ECR repository"
  type        = list(string)
  default     = ["ui", "catalog", "cart", "checkout", "orders", "load-generator"]
}

# Tags
variable "tags" {
  description = "Additional tags merged with the auto-generated common tags"
  type        = map(string)
  default     = {}
}
