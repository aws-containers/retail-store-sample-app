output "vpc_id" {
    value = aws_vpc.main.id
}

output "vpc_cidr_block" {
    value = aws_vpc.main.cidr_block
}

output "vpc_arn" {
    value = aws_vpc.main.arn
}

output "vpc_default_route_table_id" {
    value = aws_vpc.main.default_route_table_id
}

output "vpc_default_security_group_id" {
    value = aws_vpc.main.default_security_group_id
}

output "public_subnet_ids" {
    value = { for name, subnet in aws_subnet.public_subnets : name => subnet.id }
}

output "private_subnet_ids" {
    value = { for name, subnet in aws_subnet.private_subnets : name => subnet.id }
}

output "public_subnet_cidrs" {
    value = { for name, subnet in aws_subnet.public_subnets : name => subnet.cidr_block }
}

output "private_subnet_cidrs" {
    value = { for name, subnet in aws_subnet.private_subnets : name => subnet.cidr_block }
}

output "internet_gw_id" {
    value = aws_internet_gateway.main.id
}

output "public_route_table_id" {
    value = aws_route_table.public.id
}

output "public_route_table_association_ids" {
    value = { for name, assoc in aws_route_table_association.public_subnet_association : name => assoc.id }
}

output "private_route_table_id" {
    value = aws_route_table.private.id
}

output "private_route_table_association_ids" {
  description = "Private subnet route table association IDs"

  value = {
    for name, association in aws_route_table_association.private_rta :
    name => association.id
  }
}