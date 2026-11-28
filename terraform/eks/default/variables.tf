variable "environment_name" {
  description = "Name of the environment"
  type        = string
  default     = "retail-store"
}

variable "region" {
  description = "AWS region to deploy into. Pinned so the provider does not fall back to the host's env/IMDS region (which caused a region flip when run from a different host)."
  type        = string
  default     = "us-east-1"
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
