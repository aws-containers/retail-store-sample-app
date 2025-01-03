output "ecs_service_name" {
  value = aws_ecs_service.this.name

  description = "Name of the ECS service"
}

output "task_security_group_id" {
  value = aws_security_group.this.id

  description = "ID of the task security group"
}
