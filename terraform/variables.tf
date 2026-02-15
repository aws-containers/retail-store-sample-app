variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "student_id" {
  description = "Student ID for resource naming (sanitized)"
  type        = string
  default     = "alt-soe-025-0959" 
}

variable "vpc_name" {
  description = "Name of the VPC"
  type        = string
  default     = "project-bedrock-vpc"
}

variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
  default     = "project-bedrock-cluster"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}
