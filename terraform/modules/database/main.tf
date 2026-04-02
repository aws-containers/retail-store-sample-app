resource "aws_db_subnet_group" "this" {
  name       = "${var.identifier}-subnet-group"
  subnet_ids = var.subnet_ids

  tags = merge(var.tags, {
    Name = "${var.identifier}-subnet-group"
  })
}

resource "aws_security_group" "db" {
  name        = "${var.identifier}-db-sg"
  description = "Security group for RDS instance"
  vpc_id      = var.vpc_id

  dynamic "ingress" {
    for_each = toset(var.allowed_cidr_blocks)
    content {
      description = "Database ingress from CIDR ${ingress.value}"
      from_port   = var.port
      to_port     = var.port
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  dynamic "ingress" {
    for_each = toset(var.allowed_security_group_ids)
    content {
      description     = "Database ingress from security group ${ingress.value}"
      from_port       = var.port
      to_port         = var.port
      protocol        = "tcp"
      security_groups = [ingress.value]
    }
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.identifier}-db-sg"
  })
}


resource "aws_db_instance" "this" {
  identifier                 = var.identifier
  engine                     = var.engine
  engine_version             = var.engine_version
  instance_class             = var.instance_class
  allocated_storage          = var.allocated_storage
  max_allocated_storage      = var.max_allocated_storage
  db_name                    = var.db_name
  username                   = var.username
  password                   = var.password
  port                       = var.port
  db_subnet_group_name       = aws_db_subnet_group.this.name
  vpc_security_group_ids     = [aws_security_group.db.id]
  multi_az                   = var.multi_az
  publicly_accessible        = var.publicly_accessible
  storage_encrypted          = true
  backup_retention_period    = var.backup_retention_period
  skip_final_snapshot        = var.skip_final_snapshot
  final_snapshot_identifier  = var.skip_final_snapshot ? null : "${var.identifier}-final"
  deletion_protection        = !var.skip_final_snapshot
  apply_immediately          = true
  auto_minor_version_upgrade = true

  tags = var.tags
}