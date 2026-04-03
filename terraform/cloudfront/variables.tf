variable "elb_domain_name" {
  description = "Domain name for the ELB"
  type        = string
}

variable "origin_id" {
  description = "Origin ID for the CloudFront distribution"
  type        = string
}

variable "custom_header_name" {
  description = "Name of the custom header to add to the origin request"
  type        = string
}

variable "custom_header_value" {
  description = "Value of the custom header to add to the origin request"
  type        = string
}

variable "enabled" {
  description = "Whether the CloudFront distribution is enabled"
  type        = bool
  default     = true
}