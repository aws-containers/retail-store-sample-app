output "output_public_subnet_ids" {
  value       = [for subnet in aws_subnet.public_subnet : subnet.id]
  description = "Public subnet IDs"
}

output "output_private_subnet_ids" {
  value       = [for subnet in aws_subnet.private_subnet : subnet.id]
  description = "Private subnet IDs"
}

output "output_number_of_subnets" {
  value       = length(aws_subnet.public_subnet) + length(aws_subnet.private_subnet)
  description = "Total number of subnets created"
}

output "output_private_subnet" {
  value       = aws_subnet.private_subnet
  description = "Private subnet details"
}

output "output_public_subnet" {
  value       = aws_subnet.public_subnet
  description = "Public subnet details"
}
