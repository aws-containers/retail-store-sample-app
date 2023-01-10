module "orders_rds" {
  source  = "terraform-aws-modules/rds-aurora/aws"

  name = "${var.environment_name}-orders"
  engine = "aurora-mysql"
  engine_version = "5.7"
  instance_class = "db.t3.small"

  instances = {
    one = {}
  }

  vpc_id = var.vpc_id
  subnets = var.subnet_ids

  allowed_security_groups = [var.orders_security_group_id]

  master_password        = random_string.orders_db_master.result
  create_random_password = false
  database_name          = "orders"
  storage_encrypted      = true
  apply_immediately      = true
  skip_final_snapshot    = true

  create_db_parameter_group = true
  db_parameter_group_name   = "${var.environment_name}-orders"
  db_parameter_group_family = "aurora-mysql5.7"

  create_db_cluster_parameter_group = true
  db_cluster_parameter_group_name   = "${var.environment_name}-orders"
  db_cluster_parameter_group_family = "aurora-mysql5.7"

  tags             = var.tags
}

resource "random_string" "orders_db_master" {
  length  = 10
  special = false
}