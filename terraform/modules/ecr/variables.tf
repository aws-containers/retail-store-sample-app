variable "project_name" {
  description = "Project name used as a prefix for all ECR repository names"
  type        = string
}

variable "services" {
  description = "List of microservice names to create ECR repositories for"
  type        = list(string)
  default     = ["ui", "catalog", "cart", "checkout", "orders", "load-generator"]
}

variable "image_tag_mutability" {
  description = "Tag mutability setting for ECR repositories: MUTABLE or IMMUTABLE"
  type        = string
  default     = "MUTABLE"

  validation {
    condition     = contains(["MUTABLE", "IMMUTABLE"], var.image_tag_mutability)
    error_message = "image_tag_mutability must be MUTABLE or IMMUTABLE."
  }
}

variable "scan_on_push" {
  description = "Enable image vulnerability scanning automatically on every push"
  type        = bool
  default     = true
}

variable "untagged_expire_days" {
  description = "Number of days after which untagged images are expired"
  type        = number
  default     = 7
}

variable "tagged_keep_count" {
  description = "Number of tagged images (with prefix 'v') to retain per repository"
  type        = number
  default     = 10
}

variable "tags" {
  description = "Common tags to apply to all ECR resources"
  type        = map(string)
  default     = {}
}
