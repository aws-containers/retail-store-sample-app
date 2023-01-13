module "vpc_endpoints" {
  source = "terraform-aws-modules/vpc/aws//modules/vpc-endpoints"

  vpc_id             = var.vpc_id
  security_group_ids = [aws_security_group.vpc_endpoint.id]

  endpoints = {
    apprunner = {
      service = "apprunner.requests"
      # private_dns_enabled = true
      subnet_ids = var.subnet_ids
      tags       = { Name = "${var.environment_name}-apprunner" }
    },
  }

  tags = var.tags
}

resource "aws_security_group" "vpc_endpoint" {
  name        = "${var.environment_name}-vpc-endpoint"
  description = "Security group for VPC endpoint"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow all ingress"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all egress"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}