variable "environment_name" {
  type    = string
  default = "retail-store-ar"
}

variable "container_image_overrides" {
  type        = any
  default     = {}
  description = "Container image override object"
}

variable "image_repository_type" {
  description = "The type of image repository where the images will be pulled from"
  default     = "ECR_PUBLIC"
}
