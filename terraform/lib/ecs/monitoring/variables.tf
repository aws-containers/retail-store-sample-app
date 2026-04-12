variable "environment_name" {
  type        = string
  description = "Name of the environment"
}

variable "cluster_name" {
  type        = string
  description = "ECS cluster name"
}

variable "service_names" {
  type        = list(string)
  description = "List of ECS service names"
  default     = ["ui", "catalog", "carts", "orders", "checkout"]
}

variable "alb_arn_suffix" {
  type        = string
  description = "ALB ARN suffix for CloudWatch dimensions"
}

variable "target_group_arn_suffix" {
  type        = string
  description = "Target group ARN suffix for CloudWatch dimensions"
}

variable "log_group_name" {
  type        = string
  description = "Application log group name"
}

variable "log_group_arn" {
  type        = string
  description = "Application log group ARN"
}

variable "container_insights_log_group_name" {
  type        = string
  description = "Container Insights performance log group name"
  default     = ""
}

variable "container_insights_log_group_arn" {
  type        = string
  description = "Container Insights performance log group ARN"
  default     = ""
}

variable "catalog_db_cluster_id" {
  type        = string
  description = "Aurora MySQL cluster identifier for catalog"
}

variable "orders_db_cluster_id" {
  type        = string
  description = "Aurora PostgreSQL cluster identifier for orders"
}

variable "carts_dynamodb_table_name" {
  type        = string
  description = "DynamoDB table name for carts"
}

variable "checkout_elasticache_replication_group_id" {
  type        = string
  description = "ElastiCache replication group ID for checkout"
}

variable "alert_email" {
  type        = string
  description = "Email address for alarm notifications"
  default     = ""
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}

variable "application_signals_slos_enabled" {
  type        = bool
  description = "Enable Application Signals SLOs (requires services to be discovered by Application Signals first)"
  default     = false
}
