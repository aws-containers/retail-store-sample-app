output "inner" {
  value = module.vpc
}

output "availability_zones" {
  value = local.azs
}

output "vpc_cidr" {
  value = var.vpc_cidr
}