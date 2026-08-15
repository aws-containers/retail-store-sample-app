output "vpc_id" {
  value = module.vpc.vpc_id
}

output "vpc_cidr_block" {
  value = module.vpc.vpc_cidr_block
}

output "vpc_arn" {
  value = module.vpc.vpc_arn
}

output "vpc_default_route_table_id" {
  value = module.vpc.vpc_default_route_table_id
}

output "vpc_default_security_group_id" {
  value = module.vpc.vpc_default_security_group_id
}


output "public_subnet_ids" {
  value = { for name, subnet in module.vpc.public_subnet_ids : name => subnet }
}

output "private_subnet_ids" {
  value = { for name, subnet in module.vpc.private_subnet_ids : name => subnet }
}

output "public_subnet_cidrs" {
  value = { for name, subnet in module.vpc.public_subnet_cidrs : name => subnet }
}

output "private_subnet_cidrs" {
  value = { for name, subnet in module.vpc.private_subnet_cidrs : name => subnet }
}

output "internet_gw_id" {
  value = module.vpc.internet_gw_id
}

output "public_route_table_id" {
  value = module.vpc.public_route_table_id
}

output "public_route_table_association_ids" {
  value = { for name, assoc in module.vpc.public_route_table_association_ids : name => assoc }
}

output "private_route_table_id" {
  value = module.vpc.private_route_table_id
}


