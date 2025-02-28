locals {
  mq_default_user            = "default_mq_user"
  allowed_security_group_ids = concat(var.allowed_security_group_ids, [var.orders_security_group_id])
}

resource "random_password" "mq_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_+{}<>?"
}

resource "aws_mq_broker" "mq" {
  broker_name = "${var.environment_name}-orders-broker"

  engine_type                = "RabbitMQ"
  engine_version             = "3.13"
  host_instance_type         = "mq.t3.micro"
  deployment_mode            = "SINGLE_INSTANCE"
  subnet_ids                 = [var.subnet_ids[0]]
  security_groups            = [aws_security_group.mq.id]
  apply_immediately          = true
  publicly_accessible        = false
  auto_minor_version_upgrade = true

  user {
    username = local.mq_default_user
    password = random_password.mq_password.result
  }

  tags = var.tags
}

resource "aws_security_group" "mq" {
  name        = "${var.environment_name}-orders-broker"
  vpc_id      = var.vpc_id
  description = "Secure traffic to Rabbit MQ"

  tags = merge(var.tags, { Name = "${var.environment_name}-orders-broker" })

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_security_group_rule" "mq" {
  count = length(local.allowed_security_group_ids)

  type              = "ingress"
  from_port         = 5671
  to_port           = 5671
  protocol          = "tcp"
  security_group_id = aws_security_group.mq.id

  source_security_group_id = local.allowed_security_group_ids[count.index]
}
