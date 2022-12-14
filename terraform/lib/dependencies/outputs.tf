output "catalog_cluster_endpoint" {
  description = "Writer endpoint for the cluster"
  value       = module.catalog_rds.cluster_endpoint
}

output "catalog_cluster_master_password" {
  description = "The database master password"
  value       = module.catalog_rds.cluster_master_password
  sensitive   = true
}

output "catalog_cluster_master_username" {
  description = "The database master username"
  value       = module.catalog_rds.cluster_master_username
  sensitive   = true
}

output "catalog_cluster_port" {
  description = "The database port"
  value       = module.catalog_rds.cluster_port
}

output "catalog_cluster_reader_endpoint" {
  description = "A read-only endpoint for the cluster, automatically load-balanced across replicas"
  value       = module.catalog_rds.cluster_reader_endpoint
}

output "catalog_cluster_arn" {
  description = "Amazon Resource Name (ARN) of cluster"
  value       = module.catalog_rds.cluster_arn
}

output "orders_cluster_endpoint" {
  description = "Writer endpoint for the cluster"
  value       = module.orders_rds.cluster_endpoint
}

output "orders_cluster_master_password" {
  description = "The database master password"
  value       = module.orders_rds.cluster_master_password
  sensitive   = true
}

output "orders_cluster_master_username" {
  description = "The database master username"
  value       = module.orders_rds.cluster_master_username
  sensitive   = true
}

output "orders_cluster_port" {
  description = "The database port"
  value       = module.orders_rds.cluster_port
}

output "orders_cluster_reader_endpoint" {
  description = "A read-only endpoint for the cluster, automatically load-balanced across replicas"
  value       = module.orders_rds.cluster_reader_endpoint
}

output "orders_cluster_arn" {
  description = "Amazon Resource Name (ARN) of cluster"
  value       = module.orders_rds.cluster_arn
}

output "carts_dynamodb_table_arn" {
  description = "ARN of the DynamoDB table"
  value       = module.dynamodb-carts.dynamodb_table_arn
}

output "carts_dynamodb_table_id" {
  description = "ID of the DynamoDB table"
  value       = module.dynamodb-carts.dynamodb_table_id
}

output "orders_broker_id" {
  value       = module.orders_mq_broker.broker_id
  description = "AmazonMQ broker ID"
}

output "orders_broker_arn" {
  value       = module.orders_mq_broker.broker_arn
  description = "AmazonMQ broker ARN"
}

output "orders_mq_primary_console_url" {
  value       = module.orders_mq_broker.primary_console_url
  description = "AmazonMQ active web console URL"
}

output "orders_mq_passsword" {
  value     = random_password.mq_password.result
  sensitive = true
  description = "AmazonMQ Admin password."
}

output "checkout_elasticache_arn" {
    value = module.checkout-elasticache-redis.arn
    description = "Checkout Redis ElastiCache ARN."
}

output "checkout_elasticache_primary_endpoint" {
    value = module.checkout-elasticache-redis.endpoint
    description = "Checkout Redis hostname."
}

output "checkout_elasticache_reader_endpoint" {
    value = module.checkout-elasticache-redis.reader_endpoint_address
    description = "Checkout Redis hostname."
}

output "checkout_elasticache_port" {
    value = module.checkout-elasticache-redis.port
    description = "Checkout Redis port."
}