output "output_vpc_id" {
  value       = aws_vpc.vpc.id
  description = "VPC ID"
}

output "output_vpc_cidr" {
  value       = aws_vpc.vpc.cidr_block
  description = "VPC CIDR Block"
}
