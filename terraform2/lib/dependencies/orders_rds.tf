module "orders_rds" {
  source  = "terraform-aws-modules/rds-aurora/aws"
  version = "7.7.1"

  name           = "${var.environment_name}-orders"
  engine         = "aurora-postgresql"
  engine_version = "15.10"
  instance_class = "db.t3.medium"

  instances = {
    one = {}
  }

  vpc_id  = var.vpc_id
  subnets = var.subnet_ids

  allowed_security_groups = concat(var.allowed_security_group_ids, [var.orders_security_group_id])

  master_password        = random_string.orders_db_master.result
  create_random_password = false
  database_name          = "orders"
  storage_encrypted      = true
  apply_immediately      = true
  skip_final_snapshot    = true

  create_db_parameter_group = true
  db_parameter_group_name   = "${var.environment_name}-orders"
  db_parameter_group_family = "aurora-postgresql15"

  create_db_cluster_parameter_group = true
  db_cluster_parameter_group_name   = "${var.environment_name}-orders"
  db_cluster_parameter_group_family = "aurora-postgresql15"

  tags = var.tags
}

resource "random_string" "orders_db_master" {
  length  = 10
  special = false
}
