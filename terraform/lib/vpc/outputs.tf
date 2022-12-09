output "inner" {
  value = module.vpc
}

output "availability_zones" {
  value = local.azs
}