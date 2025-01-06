# Launch template for ECS container instances
resource "aws_launch_template" "ecs" {
  name_prefix   = "${var.environment_name}-${var.service_name}-ecs"
  image_id      = data.aws_ami.ecs_optimized.id
  instance_type = "t3.large"  # Adjust based on your needs (t3.small has 2 vCPU, 2GB RAM)

  user_data = base64encode(<<-EOF
    #!/bin/bash
    echo "ECS_CLUSTER=${var.environment_name}-cluster" >> /etc/ecs/ecs.config
    EOF
  )

  network_interfaces {
    associate_public_ip_address = false
    security_groups            = [aws_security_group.ecs_hosts.id]
  }

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_host.name
  }

  monitoring {
    enabled = true
  }

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name = "${var.environment_name}-${var.service_name}-ecs"
    }
  }
}

# Auto Scaling Group for ECS container instances
resource "aws_autoscaling_group" "ecs" {
  name                = "${var.environment_name}-${var.service_name}-ecs"
  vpc_zone_identifier = var.subnet_ids
  health_check_type   = "EC2"
  desired_capacity    = 1
  max_size           = 2
  min_size           = 1

  launch_template {
    id      = aws_launch_template.ecs.id
    version = "$Latest"
  }

  tag {
    key                 = "AmazonECSManaged"
    value              = true
    propagate_at_launch = true
  }
}

# Add capacity provider configuration
resource "aws_ecs_capacity_provider" "this" {
  name = "${var.environment_name}-${var.service_name}-capacity-provider"

  auto_scaling_group_provider {
    auto_scaling_group_arn = aws_autoscaling_group.ecs.arn

    managed_scaling {
      maximum_scaling_step_size = 1
      minimum_scaling_step_size = 1
      status                    = "ENABLED"
      target_capacity          = 100
    }
  }
}

# Security group for ECS hosts
resource "aws_security_group" "ecs_hosts" {
  name        = "${var.environment_name}-${var.service_name}-ecs-hosts"
  description = "Security group for ECS host instances"
  vpc_id      = var.vpc_id

  ingress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.this.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.environment_name}-${var.service_name}-ecs-hosts"
  }
}

# IAM role for ECS host instances
resource "aws_iam_role" "ecs_host" {
  name = "${var.environment_name}-${var.service_name}-ecs-host"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# IAM instance profile for ECS hosts
resource "aws_iam_instance_profile" "ecs_host" {
  name = "${var.environment_name}-${var.service_name}-ecs-host"
  role = aws_iam_role.ecs_host.name
}

# Attach required policies to ECS host role
resource "aws_iam_role_policy_attachment" "ecs_host_policy" {
  role       = aws_iam_role.ecs_host.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

# Data source for ECS-optimized AMI
data "aws_ami" "ecs_optimized" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-*"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

