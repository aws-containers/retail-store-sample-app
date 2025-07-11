variable "environment_name" {
  type        = string
  description = "Name of the environment"
}

variable "service_name" {
  type        = string
  description = "Name of the ECS service"
}

variable "cluster_arn" {
  description = "ECS cluster ARN"
  type        = string
}

variable "tags" {
  description = "List of tags to be associated with resources."
  default     = {}
  type        = any
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "subnet_ids" {
  description = "List of private subnet IDs."
  type        = list(string)
}

variable "container_image" {
  description = "Container image for the service"
  type        = string
}

variable "service_discovery_namespace_arn" {
  description = "ARN of the service discovery namespace for Service Connect"
  type        = string
}

variable "environment_variables" {
  description = "Map of environment variables for the ECS task"
  default     = {}
  type        = any
}

variable "secrets" {
  description = "Map of secrets for the ECS task"
  default     = {}
  type        = any
}

variable "additional_task_role_iam_policy_arns" {
  description = "Additional IAM policy ARNs to be added to the task role"
  default     = []
  type        = list(string)
}

variable "additional_task_execution_role_iam_policy_arns" {
  description = "Additional IAM policy ARNs to be added to the task execution role"
  default     = []
  type        = list(string)
}

variable "healthcheck_path" {
  description = "HTTP path used to health check the service"
  default     = "/health"
  type        = string
}

variable "cloudwatch_logs_group_id" {
  description = "CloudWatch logs group ID"
  type        = string
}

variable "alb_target_group_arn" {
  description = "ARN of the ALB target group the ECS service should register tasks to"
  default     = ""
  type        = string
}

variable "opentelemetry_enabled" {
  description = "Boolean value that enables OpenTelemetry."
  type        = bool
}
