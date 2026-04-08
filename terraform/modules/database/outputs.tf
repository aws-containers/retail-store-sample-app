output "db_instance_id" {
  description = "RDS instance ID."
  value       = aws_db_instance.this.id
}

output "db_instance_arn" {
  description = "RDS instance ARN."
  value       = aws_db_instance.this.arn
}

output "db_instance_endpoint" {
  description = "RDS endpoint address."
  value       = aws_db_instance.this.address
}

output "db_instance_port" {
  description = "RDS endpoint port."
  value       = aws_db_instance.this.port
}

output "db_security_group_id" {
  description = "Security group ID for the RDS instance."
  value       = aws_security_group.db.id
}
