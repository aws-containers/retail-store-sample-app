variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
}

variable "cluster_version" {
  description = "Kubernetes version for the EKS cluster"
  type        = string
  default     = "1.35"
}

variable "vpc_id" {
  description = "ID of the VPC where the EKS cluster will be deployed"
  type        = string
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs for EKS worker nodes (one per AZ)"
  type        = list(string)
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs (used for public-facing load balancers)"
  type        = list(string)
}

variable "node_instance_type" {
  description = "EC2 instance type for managed worker nodes"
  type        = string
  default     = "t3.small"
}

variable "node_min_size" {
  description = "Minimum number of worker nodes per node group"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of worker nodes per node group"
  type        = number
  default     = 3
}

variable "node_desired_size" {
  description = "Desired number of worker nodes per node group"
  type        = number
  default     = 2
}

variable "tags" {
  description = "Common tags to apply to all resources created by this module"
  type        = map(string)
  default     = {}
}
