output "output_vpc_id" {
  value       = aws_vpc.vpc.id
  description = "VPC ID"
}

output "output_vpc_cidr" {
  value       = aws_vpc.vpc.cidr_block
  description = "VPC CIDR Block"
}

output "output_public_subnets" {

  value       = module.subnet.output_public_subnet_ids
  description = "List of public subnet IDs"
}

output "output_private_subnets" {
  value       = module.subnet.output_private_subnet_ids
  description = "List of private subnet IDs"
}