module "alb_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 4.0"

  name        = "${var.environment_name}-ui"
  description = "UI ALB security group"
  vpc_id      = var.vpc_id

  ingress_rules       = ["http-80-tcp"]
  ingress_cidr_blocks = ["0.0.0.0/0"]

  egress_rules       = ["all-all"]
  egress_cidr_blocks = ["0.0.0.0/0"]
}

module "alb" {
  source  = "terraform-aws-modules/alb/aws"
  version = "~> 8.0"

  name = "${var.environment_name}-ui"

  load_balancer_type = "application"

  vpc_id          = var.vpc_id
  subnets         = var.public_subnet_ids
  security_groups = [module.alb_sg.security_group_id]

  http_tcp_listeners = [
    {
      port               = 80
      protocol           = "HTTP"
      target_group_index = 0
    }
  ]

  target_groups = [
    {
      name                 = "ui-application"
      backend_protocol     = "HTTP"
      backend_port         = 8080
      target_type          = "ip"
      deregistration_delay = 30
      health_check = {
        enabled             = true
        interval            = 30
        path                = "/actuator/health"
        port                = "traffic-port"
        healthy_threshold   = 2
        unhealthy_threshold = 3
        timeout             = 5
        protocol            = "HTTP"
      }
    }
  ]
}
