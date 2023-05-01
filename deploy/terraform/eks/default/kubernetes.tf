locals {
  istio_labels = {
    istio-injection = "enabled"
  }
}

resource "null_resource" "cluster_blocker" {
  triggers = {
    "blocker" = module.retail_app_eks.cluster_blocker_id
  }
}

resource "null_resource" "addons_blocker" {
  triggers = {
    "blocker" = module.retail_app_eks.addons_blocker_id
  }
}

resource "kubernetes_namespace_v1" "assets" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "assets"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "assets" {
  name       = "assets"
  chart      = "../../../kubernetes/charts/assets"

  namespace  = kubernetes_namespace_v1.assets.metadata[0].name
  values = [
    templatefile("${path.module}/values/assets.yaml", { 
      tracing_enabled = var.tracing_enabled
    })
  ]
}

resource "kubernetes_namespace_v1" "catalog" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "catalog"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "catalog" {
  name       = "catalog"
  chart      = "../../../kubernetes/charts/catalog"

  namespace  = kubernetes_namespace_v1.catalog.metadata[0].name

  values = [
    templatefile("${path.module}/values/catalog.yaml", { 
      tracing_enabled = var.tracing_enabled
      database_endpoint = "${module.dependencies.catalog_db_endpoint}:${module.dependencies.catalog_db_port}"
      database_username = module.dependencies.catalog_db_master_username
      database_password = module.dependencies.catalog_db_master_password
      security_group_id = aws_security_group.catalog.id
    })
  ]
}

resource "kubernetes_namespace_v1" "carts" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "carts"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "carts" {
  name       = "carts"
  chart      = "../../../kubernetes/charts/carts"

  namespace  = kubernetes_namespace_v1.carts.metadata[0].name

  values = [
    templatefile("${path.module}/values/carts.yaml", { 
      tracing_enabled = var.tracing_enabled
      role_arn = module.iam_assumable_role_carts.iam_role_arn
      table_name = module.dependencies.carts_dynamodb_table_name 
    })
  ]
}

resource "kubernetes_namespace_v1" "checkout" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "checkout"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "checkout" {
  name       = "checkout"
  chart      = "../../../kubernetes/charts/checkout"

  namespace  = kubernetes_namespace_v1.checkout.metadata[0].name

  values = [
    templatefile("${path.module}/values/checkout.yaml", { 
      tracing_enabled = var.tracing_enabled
      redis_address     = module.dependencies.checkout_elasticache_primary_endpoint
      redis_port        = module.dependencies.checkout_elasticache_port
      security_group_id = aws_security_group.checkout.id
    })
  ]
}

resource "kubernetes_namespace_v1" "orders" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "orders"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "orders" {
  name       = "orders"
  chart      = "../../../kubernetes/charts/orders"

  namespace  = kubernetes_namespace_v1.orders.metadata[0].name

  values = [
    templatefile("${path.module}/values/orders.yaml", { 
      tracing_enabled = var.tracing_enabled
      database_endpoint = "jdbc:mariadb://${module.dependencies.orders_db_endpoint}:${module.dependencies.orders_db_port}/${module.dependencies.orders_db_database_name}"
      database_username = module.dependencies.orders_db_master_username
      database_password = module.dependencies.orders_db_master_password
      rabbitmq_endpoint = module.dependencies.mq_broker_endpoint
      rabbitmq_username = module.dependencies.mq_user
      rabbitmq_password = module.dependencies.mq_password
      security_group_id = aws_security_group.orders.id
    })
  ]
}

resource "kubernetes_namespace_v1" "ui" {
  depends_on = [
    null_resource.addons_blocker
  ]

  metadata {
    name = "ui"

    labels = var.istio_enabled ? local.istio_labels : {}
  }
}

resource "helm_release" "ui" {
  name       = "ui"
  chart      = "../../../kubernetes/charts/ui"

  namespace  = kubernetes_namespace_v1.ui.metadata[0].name

  values = [
    templatefile("${path.module}/values/ui.yaml", {
      tracing_enabled = var.tracing_enabled
      istio_enabled = var.istio_enabled
    })
  ]
}

resource "kubernetes_namespace_v1" "opentelemetry" {
  count = var.tracing_enabled ? 1 : 0
  depends_on = [
    null_resource.addons_blocker
  ]
  metadata {
    name = "opentelemetry"
  }
}

resource "helm_release" "adot-xray" {

  count = var.tracing_enabled ? 1 : 0
  name       = "adot-xray"
  chart      = "../../../kubernetes/charts/opentelemetry"

  namespace  = kubernetes_namespace_v1.opentelemetry[0].metadata[0].name

  values = [
    templatefile("${path.module}/values/adot-xray.yaml", { 
      adot_xray_role_arn = module.iam_assumable_role_adot_xray.iam_role_arn
    })
  ]
}