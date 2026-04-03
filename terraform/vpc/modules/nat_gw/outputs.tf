output "output_first_nat_gateway_id" {
  value       = aws_nat_gateway.nat_gw.id
  description = "ID of the first NAT Gateway created"
}

output "output_all_nat_ips" {
  value       = aws_eip.nat_eip.public_ip
  description = "Public IP addresses of all NAT Gateways created"
}