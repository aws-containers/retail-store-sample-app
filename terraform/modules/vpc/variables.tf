variable "project_name" {
  type = string

}

variable "environment" {
  type = string
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod"
  }
}
variable "vpc_cidr" {
  type = string
  validation {
    condition     = can(cidrsubnet(var.vpc_cidr, 8, 0))
    error_message = "Invalid CIDR block for VPC"
  }
}

variable "public_subnet" {
  type = map(object({
    cidr_block        = string
    availability_zone = string
  }))
}

variable "private_subnet" {
  type = map(object({
    cidr_block        = string
    availability_zone = string
  }))
}







