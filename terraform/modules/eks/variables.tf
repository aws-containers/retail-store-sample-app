variable "project_name" {
  type = string
}

variable "env" {
  type = string
  validation {
    condition = contains(
      ["dev", "staging", "prod"],
      var.env
    )

    error_message = "Environment must be dev, staging, or prod."
  }
}


variable "private_subnet" {
  type = list(string)
}