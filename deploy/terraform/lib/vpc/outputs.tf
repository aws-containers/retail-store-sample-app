output "inner" {
  value       = module.vpc
  description = "Outputs from the VPC module"
}

output "availability_zones" {
  value       = local.azs
  description = "List of availability zones where the VPC was created"
}

output "vpc_cidr" {
  value       = var.vpc_cidr
  description = "CIDR block of the VPC"
}