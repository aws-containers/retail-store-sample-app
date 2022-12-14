variable "environment_name" {
  # default = "test"
}

variable "vpc_id" {
  type = string
  # default = "vpc-e4678d9f"
}

variable "subnets" {
  description = "List of subnet IDs used by database subnet group created"
  type        = list(string)
  # default = ["subnet-07d7fba3c2caa131b", "subnet-865eb9da"]
}