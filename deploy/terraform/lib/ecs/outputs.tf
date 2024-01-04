output "ui_service_url" {
  description = "URL of the UI component"
  value       = "http://${module.alb.lb_dns_name}"
}

output "catalog_security_group_id" {
  value = module.catalog_service.task_security_group_id
}

output "checkout_security_group_id" {
  value = module.checkout_service.task_security_group_id
}

output "orders_security_group_id" {
  value = module.orders_service.task_security_group_id
}
