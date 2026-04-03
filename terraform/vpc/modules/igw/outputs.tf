output "output_igw_id" {
  value       = aws_internet_gateway.igw.id
  description = "Internet Gateway ID"
}

output "output_igw_arn" {
  value       = aws_internet_gateway.igw.arn
  description = "Internet Gateway ARN"
}
