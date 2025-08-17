locals {
  environment = jsonencode(concat(
    [for k, v in var.environment_variables : {
      "name" : k,
      "value" : v
    }],
    var.opentelemetry_enabled ? [
      {
        "name" : "OTEL_SDK_DISABLED",
        "value" : "false"
      },
      {
        "name" : "OTEL_EXPORTER_OTLP_PROTOCOL",
        "value" : "http/protobuf"
      },
      {
        "name" : "OTEL_RESOURCE_PROVIDERS_AWS_ENABLED",
        "value" : "true"
      },
      {
        "name" : "OTEL_METRICS_EXPORTER",
        "value" : "none"
      },
      {
        "name" : "OTEL_JAVA_GLOBAL_AUTOCONFIGURE_ENABLED",
        "value" : "true"
      },
      {
        "name" : "OTEL_EXPORTER_OTLP_ENDPOINT",
        "value" : "http://localhost:4318"
      },
      {
        "name" : "OTEL_PROPAGATORS",
        "value" : "tracecontext,baggage"
      },
      {
        "name" : "OTEL_SERVICE_NAME",
        "value" : var.service_name
      }
    ] : []
  ))

  secrets = jsonencode([for k, v in var.secrets : {
    "name" : k,
    "valueFrom" : v
  }])

  base_container = {
    name  = "${var.service_name}-service"
    image = var.container_image
    portMappings = [
      {
        containerPort = 8080
        hostPort      = 8080
        name          = "${var.service_name}-service"
        protocol      = "tcp"
      }
    ]
    essential              = true
    networkMode            = "awsvpc"
    readonlyRootFilesystem = false
    environment            = jsondecode(local.environment)
    secrets                = jsondecode(local.secrets)
    cpu                    = 0
    mountPoints            = []
    volumesFrom            = []
    healthCheck = {
      command     = ["CMD-SHELL", "curl -f http://localhost:8080${var.healthcheck_path} || exit 1"]
      interval    = 10
      startPeriod = 60
      retries     = 3
      timeout     = 5
    }
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = var.cloudwatch_logs_group_id
        "awslogs-region"        = data.aws_region.current.name
        "awslogs-stream-prefix" = "${var.service_name}-service"
      }
    }
  }

  otel_container = {
    name      = "cloudwatch-agent"
    image     = "public.ecr.aws/cloudwatch-agent/cloudwatch-agent:latest"
    essential = true
    environment = [
      {
        name = "CW_CONFIG_CONTENT"
        value = jsonencode({
          agent = {}
          traces = {
            traces_collected = {
              otlp = {}
            }
          }
        })
      }
    ]
    portMappings = [
      {
        containerPort = 4318
        protocol      = "tcp"
      }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = var.cloudwatch_logs_group_id
        "awslogs-region"        = data.aws_region.current.name
        "awslogs-stream-prefix" = "cloudwatch-agent"
      }
    }
  }

  containers = concat(
    [local.base_container],
    var.opentelemetry_enabled ? [local.otel_container] : []
  )
}

data "aws_region" "current" {}

resource "aws_ecs_task_definition" "this" {
  family                   = "${var.environment_name}-${var.service_name}"
  container_definitions    = jsonencode(local.containers)
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "1024"
  memory                   = "2048"
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  task_role_arn            = aws_iam_role.task_role.arn
}

resource "aws_ecs_service" "this" {
  name                   = var.service_name
  cluster                = var.cluster_arn
  task_definition        = aws_ecs_task_definition.this.arn
  desired_count          = 1
  launch_type            = "FARGATE"
  enable_execute_command = true
  wait_for_steady_state  = true

  network_configuration {
    security_groups  = [aws_security_group.this.id]
    subnets          = var.subnet_ids
    assign_public_ip = false
  }

  service_connect_configuration {
    enabled   = true
    namespace = var.service_discovery_namespace_arn
    service {
      client_alias {
        dns_name = var.service_name
        port     = "80"
      }
      discovery_name = var.service_name
      port_name      = "${var.service_name}-service"
    }
  }

  dynamic "load_balancer" {
    for_each = var.alb_target_group_arn == "" ? [] : [1]

    content {
      target_group_arn = var.alb_target_group_arn
      container_name   = "${var.service_name}-service"
      container_port   = 8080
    }
  }

  tags = var.tags
}
