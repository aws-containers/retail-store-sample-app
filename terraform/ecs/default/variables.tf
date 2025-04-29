variable "datadog_api_key" {
  description = "Datadog API Key"
  type        = string
  sensitive   = true
}


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


variable "account_id" {
  description = "The AWS account ID"
  type        = string
}
