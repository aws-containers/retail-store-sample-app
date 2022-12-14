variable "environment_name" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  description = "List of subnet IDs used by database subnet group created"
  type        = list(string)
}

variable "tags" {
  description = "A map of tags to add to all resources"
  default     = {}
}

variable "availability_zones" {
  type = list(string)
}
