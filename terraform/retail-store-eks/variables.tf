variable "aws_region" {
  type        = string
  description = "AWS region to deploy into"
  default     = "us-east-1"
}

variable "environment" {
  type        = string
  description = "Deployment environment label (used in tags)"
  default     = "production"
}

variable "cluster_name" {
  type        = string
  description = "Name of the EKS cluster"
  default     = "retail-store-eks"
}

variable "cluster_role_name" {
  type        = string
  description = "Name of the EKS cluster IAM role"
  default     = "retailStoreEksClusterRole"
}

variable "node_role_name" {
  type        = string
  description = "Name of the EKS worker node IAM role"
  default     = "retailStoreEksWorkerNodeRole"
}

variable "additional_policy_name" {
  type        = string
  description = "Name of the additional IAM policy attached to node and cluster roles"
  default     = "retailStoreEksPolicy"
}

variable "node_group_desired_capacity" {
  type        = number
  description = "Desired number of worker nodes"
  default     = 3
}

variable "node_group_max_size" {
  type        = number
  description = "Maximum number of worker nodes"
  default     = 4
}

variable "node_group_min_size" {
  type        = number
  description = "Minimum number of worker nodes"
  default     = 1
}

variable "node_instance_type" {
  type        = string
  description = "EC2 instance type for worker nodes"
  default     = "t3.medium"
}

variable "image_tag" {
  type        = string
  description = "Docker image tag to deploy for all services"
  default     = "latest"
}

variable "alb_controller_chart_version" {
  type        = string
  description = "Helm chart version for aws-load-balancer-controller"
  default     = "1.8.1"
}

variable "github_org" {
  type        = string
  description = "GitHub organisation or user that owns the repository (used for OIDC trust)"
  default     = "abhay09jain"
}

variable "github_repo" {
  type        = string
  description = "GitHub repository name (used for OIDC trust)"
  default     = "retail-store-sample-app"
}

variable "openai_api_key" {
  type        = string
  description = "OpenAI API key; when set, enables the AI chat feature in the UI service"
  default     = ""
  sensitive   = true
}
