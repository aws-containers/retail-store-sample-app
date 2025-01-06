variable "environment_name" {
  type = string
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "public_subnet_tags" {
  type    = map
  default = {}
}

variable "private_subnet_tags" {
  type    = map
  default = {}
}

variable "tags" {
  default = {}
}