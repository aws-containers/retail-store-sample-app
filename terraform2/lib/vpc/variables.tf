variable "environment_name" {
  type        = string
  description = "Name of the environment"
}

variable "vpc_cidr" {
  type        = string
  default     = "10.0.0.0/16"
  description = "CIDR block for the VPC"
}

variable "public_subnet_tags" {
  type        = map(any)
  default     = {}
  description = "Additional tags to apply to public subnets"
}

variable "private_subnet_tags" {
  type        = map(any)
  default     = {}
  description = "Additional tags to apply to private subnets"
}

variable "tags" {
  description = "List of tags to be associated with resources."
  default     = {}
  type        = any
}