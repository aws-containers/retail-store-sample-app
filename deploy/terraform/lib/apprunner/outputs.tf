output "ui_service_url" {
  description = "URL of the UI component"
  value       = module.app_runner_ui.service_url
}

output "catalog_security_group_id" {
  value       = aws_security_group.catalog.id
}

output "checkout_security_group_id" {
  value       = aws_security_group.checkout.id
}

output "orders_security_group_id" {
  value       = aws_security_group.orders.id
}