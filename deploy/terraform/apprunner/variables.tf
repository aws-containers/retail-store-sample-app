variable "environment_name" {
  type        = string
  default     = "retail-store-ar"
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


variable "image_repository_type" {
  type        = string
  description = "The type of image repository where the images will be pulled from"
  default     = "ECR_PUBLIC"
}
