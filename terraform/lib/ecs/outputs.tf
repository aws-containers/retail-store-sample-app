output "ui_service_url" {
  description = "URL of the UI component"
  value       = "http://${module.alb.lb_dns_name}"
}

output "catalog_security_group_id" {
  value       = module.catalog_service.task_security_group_id
  description = "Security group ID of the catalog service"
}

output "checkout_security_group_id" {
  value       = module.checkout_service.task_security_group_id
  description = "Security group ID of the checkout service"
}

output "orders_security_group_id" {
  value       = module.orders_service.task_security_group_id
  description = "Security group ID of the orders service"
}
