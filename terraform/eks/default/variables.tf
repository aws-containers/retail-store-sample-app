variable "environment_name" {
  description = "Name of the environment"
  type        = string
  default     = "retail-store"
}

variable "istio_enabled" {
  description = "Boolean value that enables istio."
  type        = bool
  default     = false
}

variable "opentelemetry_enabled" {
  description = "Boolean value that enables OpenTelemetry."
  type        = bool
  default     = false
}

variable "search_enabled" {
  description = "Boolean value that enables Search functionality."
  type        = bool
  default     = false
}

variable "search_username" {
  description = "Search provider username"
  type        = string
  default     = "admin"
}

variable "search_provider" {
  description = "Search provider"
  type        = string
  default     = "self-hosted"

  validation {
    condition     = contains(["self-hosted", "aws"], var.search_provider)
    error_message = "Environment must be one of: \"aws\" or \"self-hosted\"."
  }
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
