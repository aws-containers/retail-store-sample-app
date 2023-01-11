resource "kubernetes_namespace_v1" "assets" {
  metadata {
    name = "assets"
  }
}

resource "kubernetes_namespace_v1" "catalog" {
  metadata {
    name = "catalog"
  }
}

resource "kubernetes_config_map_v1" "catalog_db" {
  metadata {
    name      = "catalog-db"
    namespace = kubernetes_namespace_v1.catalog.metadata[0].name
  }

  data = {
    endpoint = "${module.dependencies.catalog_db_endpoint}:${module.dependencies.catalog_db_port}"
    name     = module.dependencies.catalog_db_database_name
  }
}

resource "kubernetes_secret_v1" "catalog_db" {
  metadata {
    name      = "catalog-db"
    namespace = kubernetes_namespace_v1.catalog.metadata[0].name
  }

  data = {
    username = module.dependencies.catalog_db_master_username
    password = module.dependencies.catalog_db_master_password
  }
}

resource "kubernetes_namespace_v1" "carts" {
  metadata {
    name = "carts"
  }
}

resource "kubernetes_config_map_v1" "carts_db" {
  metadata {
    name      = "carts-db"
    namespace = kubernetes_namespace_v1.carts.metadata[0].name
  }

  data = {
    table = module.dependencies.carts_dynamodb_table_name
  }
}

resource "kubernetes_namespace_v1" "checkout" {
  metadata {
    name = "checkout"
  }
}

resource "kubernetes_config_map_v1" "checkout_redis" {
  metadata {
    name      = "checkout-redis"
    namespace = kubernetes_namespace_v1.checkout.metadata[0].name
  }

  data = {
    endpoint = "redis://${module.dependencies.checkout_elasticache_primary_endpoint}:${module.dependencies.checkout_elasticache_port}"
  }
}

resource "kubernetes_namespace_v1" "orders" {
  metadata {
    name = "orders"
  }
}

resource "kubernetes_config_map_v1" "orders_db" {
  metadata {
    name      = "orders-db"
    namespace = kubernetes_namespace_v1.orders.metadata[0].name
  }

  data = {
    endpoint = "jdbc:mariadb://${module.dependencies.orders_db_endpoint}:${module.dependencies.orders_db_port}/${module.dependencies.orders_db_database_name}"
    name     = module.dependencies.orders_db_database_name
  }
}

resource "kubernetes_secret_v1" "orders_db" {
  metadata {
    name      = "orders-db"
    namespace = kubernetes_namespace_v1.orders.metadata[0].name
  }

  data = {
    username = module.dependencies.orders_db_master_username
    password = module.dependencies.orders_db_master_password
  }
}

resource "kubernetes_config_map_v1" "orders_mq" {
  metadata {
    name      = "orders-mq"
    namespace = kubernetes_namespace_v1.orders.metadata[0].name
  }

  data = {
    endpoint = module.dependencies.mq_broker_endpoint
  }
}

resource "kubernetes_secret_v1" "orders_mq" {
  metadata {
    name      = "orders-mq"
    namespace = kubernetes_namespace_v1.orders.metadata[0].name
  }

  data = {
    username = module.dependencies.mq_user
    password = module.dependencies.mq_password
  }
}

resource "kubernetes_namespace_v1" "ui" {
  metadata {
    name = "ui"
  }
}
