variable "environment_name" {
  type = string
}

output "result" {
  value = {
    environment-name = var.environment_name
    created-by       = "retail-store-sample-app"
  }
}