variable "repo_name" {
  type = set(string)
   validation {
    condition     = length(var.repo_name) > 0
    error_message = "At least one ECR repository name must be provided."
  }
}

variable "project_name" {
  type = string
}

variable "env" {
  type = string
  validation {
    condition = contains(
      ["dev", "stage", "prod"],
      var.env
    )

    error_message = "Environment must be dev, stage, or prod."
  }
}

variable "untagged_image_expiration_days" {
  type = number
  default = 7
  validation {
    condition     = var.untagged_image_expiration_days >= 1
    error_message = "Expiration days must be at least 1."
  }
}

variable "maximum_image_count" {
  type        = number
  default     = 20

  validation {
    condition     = var.maximum_image_count >= 1
    error_message = "Maximum image count must be at least 1."
  }
}





