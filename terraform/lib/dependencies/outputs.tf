output "catalog_db_endpoint" {
  description = "Writer endpoint for the catalog database"
  value       = module.catalog_rds.cluster_endpoint
}

output "catalog_db_database_name" {
  description = "Database name for the catalog database"
  value       = module.catalog_rds.cluster_database_name
}

output "catalog_db_master_password" {
  description = "Master password for the catalog database"
  value       = module.catalog_rds.cluster_master_password
  sensitive   = true
}

output "catalog_db_master_username" {
  description = "Master username for the catalog database"
  value       = module.catalog_rds.cluster_master_username
  sensitive   = true
}

output "catalog_db_port" {
  description = "Port for the catalog database"
  value       = module.catalog_rds.cluster_port
}

output "catalog_db_reader_endpoint" {
  description = "A read-only endpoint for the catalog database"
  value       = module.catalog_rds.cluster_reader_endpoint
}

output "catalog_db_arn" {
  description = "ARN for the catalog database"
  value       = module.catalog_rds.cluster_arn
}

output "orders_db_endpoint" {
  description = "Writer endpoint for the orders database"
  value       = module.orders_rds.cluster_endpoint
}

output "orders_db_database_name" {
  description = "Database name for the orders database"
  value       = module.orders_rds.cluster_database_name
}

output "orders_db_master_password" {
  description = "Master password for the orders database"
  value       = module.orders_rds.cluster_master_password
  sensitive   = true
}

output "orders_db_master_username" {
  description = "Master username for the orders database"
  value       = module.orders_rds.cluster_master_username
  sensitive   = true
}

output "orders_db_port" {
  description = "Port for the orders database"
  value       = module.orders_rds.cluster_port
}

output "orders_db_reader_endpoint" {
  description = "Read-only endpoint for the orders database"
  value       = module.orders_rds.cluster_reader_endpoint
}

output "orders_db_arn" {
  description = "ARN for the orders database"
  value       = module.orders_rds.cluster_arn
}

output "carts_dynamodb_table_arn" {
  description = "ARN of the carts DynamoDB table"
  value       = module.dynamodb_carts.dynamodb_table_arn
}

output "carts_dynamodb_table_name" {
  description = "Name of the carts DynamoDB table"
  value       = module.dynamodb_carts.dynamodb_table_id
}

output "carts_dynamodb_policy_arn" {
  description = "ARN of IAM policy to access carts DynamoDB table"
  value       = aws_iam_policy.carts_dynamo.arn
}

output "mq_broker_id" {
  value       = aws_mq_broker.mq.id
  description = "AmazonMQ broker ID"
}

output "mq_broker_arn" {
  value       = aws_mq_broker.mq.arn
  description = "AmazonMQ broker ARN"
}

output "mq_broker_endpoint" {
  value       = aws_mq_broker.mq.instances[0].endpoints[0]
  description = "AmazonMQ broker endpoint"
}

output "mq_password" {
  value       = random_password.mq_password.result
  sensitive   = true
  description = "AmazonMQ Admin password."
}

output "mq_user" {
  value       = local.mq_default_user
  description = "AmazonMQ Admin user"
}

output "checkout_elasticache_arn" {
  value       = module.checkout_elasticache_redis.arn
  description = "Checkout Redis ElastiCache ARN."
}

output "checkout_elasticache_primary_endpoint" {
  value       = module.checkout_elasticache_redis.endpoint
  description = "Checkout Redis hostname"
}

output "checkout_elasticache_reader_endpoint" {
  value       = module.checkout_elasticache_redis.reader_endpoint_address
  description = "Checkout Redis reader hostname"
}

output "checkout_elasticache_port" {
  value       = module.checkout_elasticache_redis.port
  description = "Checkout Redis port"
}
