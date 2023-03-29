variable "environment_name" {
  type = string
}

variable "tags" {
  description = "List of tags to be associated with resources."
  default     = {}
}

variable "vpc_id" {
  description = "VPC ID used to create EKS cluster."
  type        = string
}

variable "vpc_cidr" {
  description = "VPC ID used to create EKS cluster."
  type        = string
}

variable "subnet_ids" {
  description = "List of private subnet IDs used by EKS cluster nodes."
  type        = list(string)
}

variable "container_image_overrides" {
  type        = any
  default     = {}
  description = "Container image override object"
}

variable "catalog_db_endpoint" {
  type = string
}

variable "catalog_db_port" {
  type = string
}

variable "catalog_db_name" {
  type = string
}

variable "catalog_db_username" {
  type = string
}

variable "catalog_db_password" {
  type = string
}

variable "carts_dynamodb_table_name" {
  type = string
}

variable "carts_dynamodb_policy_arn" {
  type = string
}

variable "orders_db_endpoint" {
  type = string
}

variable "orders_db_port" {
  type = string
}

variable "orders_db_name" {
  type = string
}

variable "orders_db_username" {
  type = string
}

variable "orders_db_password" {
  type = string
}

variable "checkout_redis_endpoint" {
  type = string
}

variable "checkout_redis_port" {
  type = string
}

variable "mq_endpoint" {
  type = string
}

variable "mq_username" {
  type = string
}

variable "mq_password" {
  type = string
}