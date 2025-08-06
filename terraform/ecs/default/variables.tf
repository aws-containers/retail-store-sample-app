variable "environment_name" {
  type        = string
  default     = "retail-store-ecs"
  description = "Name of the environment"
}

variable "container_image_overrides" {
  type = object({
    default_repository = optional(string)
    default_tag        = optional(string)

    ui       = optional(string)
    catalog  = optional(string)
    cart     = optional(string)
    checkout = optional(string)
    orders   = optional(string)
  })
  default     = {}
  description = "Object that encapsulates any overrides to default values"
}

variable "opentelemetry_enabled" {
  type        = bool
  default     = false
  description = "Boolean value that enables OpenTelemetry."
}

variable "container_insights_setting" {
  type        = string
  default     = "enhanced"
  description = "Container Insights setting for ECS cluster (enhanced or disabled)"

  validation {
    condition     = contains(["enhanced", "disabled"], var.container_insights_setting)
    error_message = "container_insights_setting must be either 'enhanced' or 'disabled'"
  }
}

variable "lifecycle_events_enabled" {
  type        = bool
  default     = false
  description = "Enable ECS lifecycle events to CloudWatch Logs"

  validation {
    condition     = !var.lifecycle_events_enabled || var.container_insights_setting == "enhanced"
    error_message = "lifecycle_events_enabled can only be true when container_insights_setting is 'enhanced'"
  }
}