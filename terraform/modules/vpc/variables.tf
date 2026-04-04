# VPC VARIABLES
variable "vpc_cidr" {
  type = string
}

variable "vpc_subnet_az_id" {
  type = list(string)
}
