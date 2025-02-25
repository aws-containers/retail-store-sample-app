output "application_url" {
  description = "URL where the application can be accessed"
  value       = module.retail_app_apprunner.ui_service_url
}
