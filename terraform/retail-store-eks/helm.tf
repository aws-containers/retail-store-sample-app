####################################################################
# Helm releases — all five retail-store services
# Image repositories point to private ECR; tag comes from var.image_tag
####################################################################

locals {
  chart_base = "${path.module}/../../src"
}

# ---- ui ----

resource "helm_release" "ui" {
  name      = "ui"
  chart     = "${local.chart_base}/ui/chart"
  namespace = "default"

  set {
    name  = "image.repository"
    value = "${local.ecr_registry}/retail-store-sample-ui"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }
  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  # ALB ingress so the UI is reachable from the internet
  set {
    name  = "ingress.enabled"
    value = "true"
  }
  set {
    name  = "ingress.annotations.kubernetes\\.io/ingress\\.class"
    value = "alb"
  }
  set {
    name  = "ingress.annotations.alb\\.ingress\\.kubernetes\\.io/scheme"
    value = "internet-facing"
  }
  set {
    name  = "ingress.annotations.alb\\.ingress\\.kubernetes\\.io/target-type"
    value = "ip"
  }
  set {
    name  = "ingress.annotations.alb\\.ingress\\.kubernetes\\.io/healthcheck-path"
    value = "/actuator/health/liveness"
  }

  set {
    name  = "app.endpoints.catalog"
    value = "http://catalog:80"
  }
  set {
    name  = "app.endpoints.carts"
    value = "http://carts:80"
  }
  set {
    name  = "app.endpoints.checkout"
    value = "http://checkout:80"
  }
  set {
    name  = "app.endpoints.orders"
    value = "http://orders:80"
  }

  # OpenAI chat — enabled when the API key is provided
  set {
    name  = "app.chat.enabled"
    value = var.openai_api_key != "" ? "true" : "false"
  }
  set {
    name  = "app.chat.provider"
    value = "openai"
  }
  set {
    name  = "app.chat.model"
    value = "gpt-4o-mini"
  }
  set {
    name  = "app.chat.openai.baseUrl"
    value = "https://api.openai.com"
  }
  set {
    name  = "app.chat.openai.apiKey"
    value = var.openai_api_key
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
  ]
}

# ---- catalog ----

resource "helm_release" "catalog" {
  name      = "catalog"
  chart     = "${local.chart_base}/catalog/chart"
  namespace = "default"

  set {
    name  = "image.repository"
    value = "${local.ecr_registry}/retail-store-sample-catalog"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }
  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  # Use the in-cluster MySQL sidecar backed by private ECR
  set {
    name  = "mysql.create"
    value = "true"
  }
  set {
    name  = "mysql.image.repository"
    value = "${local.ecr_registry}/retail-store-sample-mysql"
  }
  set {
    name  = "mysql.image.tag"
    value = "8.0"
  }
  set {
    name  = "app.persistence.provider"
    value = "mysql"
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
  ]
}

# ---- cart ----

resource "helm_release" "cart" {
  name      = "carts"
  chart     = "${local.chart_base}/cart/chart"
  namespace = "default"

  set {
    name  = "image.repository"
    value = "${local.ecr_registry}/retail-store-sample-cart"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }
  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  # Use the in-cluster DynamoDB Local sidecar backed by private ECR
  set {
    name  = "dynamodb.create"
    value = "true"
  }
  set {
    name  = "dynamodb.image.repository"
    value = "${local.ecr_registry}/retail-store-sample-dynamodb-local"
  }
  set {
    name  = "dynamodb.image.tag"
    value = "1.25.1"
  }
  set {
    name  = "app.persistence.provider"
    value = "dynamodb"
  }
  set {
    name  = "app.persistence.dynamodb.createTable"
    value = "true"
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
  ]
}

# ---- checkout ----

resource "helm_release" "checkout" {
  name      = "checkout"
  chart     = "${local.chart_base}/checkout/chart"
  namespace = "default"

  set {
    name  = "image.repository"
    value = "${local.ecr_registry}/retail-store-sample-checkout"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }
  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  # Use the in-cluster Redis sidecar backed by private ECR
  set {
    name  = "redis.create"
    value = "true"
  }
  set {
    name  = "redis.image.repository"
    value = "${local.ecr_registry}/retail-store-sample-redis"
  }
  set {
    name  = "redis.image.tag"
    value = "6.0-alpine"
  }
  set {
    name  = "app.persistence.provider"
    value = "redis"
  }
  set {
    name  = "app.endpoints.orders"
    value = "http://orders:80"
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
  ]
}

# ---- orders ----

resource "helm_release" "orders" {
  name      = "orders"
  chart     = "${local.chart_base}/orders/chart"
  namespace = "default"

  set {
    name  = "image.repository"
    value = "${local.ecr_registry}/retail-store-sample-orders"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }
  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  # Use the in-cluster PostgreSQL sidecar backed by private ECR
  set {
    name  = "postgresql.create"
    value = "true"
  }
  set {
    name  = "postgresql.image.repository"
    value = "${local.ecr_registry}/retail-store-sample-postgres"
  }
  set {
    name  = "postgresql.image.tag"
    value = "16.1"
  }

  # Use the in-cluster RabbitMQ sidecar backed by private ECR
  set {
    name  = "rabbitmq.create"
    value = "true"
  }
  set {
    name  = "rabbitmq.image.repository"
    value = "${local.ecr_registry}/retail-store-sample-rabbitmq"
  }
  set {
    name  = "rabbitmq.image.tag"
    value = "3-management"
  }

  set {
    name  = "app.persistence.provider"
    value = "postgres"
  }
  set {
    name  = "app.messaging.provider"
    value = "rabbitmq"
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
  ]
}
