output "output_private_route_table_id" {
  value       = aws_route_table.private_route_table.id
  description = "Private Route Table ID"
}

output "output_public_route_table_id" {
  value       = aws_route_table.public_route_table.id
  description = "Public Route Table ID"
}