variable "environment_name" {
  type        = string
  description = "Name of the environment"
}

variable "vpc_id" {
  type        = string
  description = "VPC ID for the resources"
}

variable "subnet_ids" {
  description = "List of subnet IDs used by database subnet group created"
  type        = list(string)
}

variable "tags" {
  description = "List of tags to be associated with resources."
  default     = {}
  type        = any
}

variable "catalog_security_group_id" {
  type        = string
  description = "Security group ID of the catalog component"
}

variable "orders_security_group_id" {
  type        = string
  description = "Security group ID of the orders component"
}

variable "checkout_security_group_id" {
  type        = string
  description = "Security group ID of the checkout component"
}

variable "allowed_security_group_ids" {
  type        = list(string)
  default     = []
  description = "List of additional allowed security group IDs"
}

variable "catalog_opensearch_security_group_id" {
  type        = string
  description = "Security group ID for the catalog OpenSearch domain"
}

variable "catalog_search_enabled" {
  type        = bool
  description = "Boolean value that enables catalog OpenSearch functionality"
  default     = false
}

variable "catalog_search_provider" {
  type        = string
  description = "Catalog search provider"
  default     = "self-hosted"
}

variable "catalog_search_username" {
  type        = string
  description = "Catalog search usename"
  default     = "admin"
}

