module "catalog_rds" {
  source  = "terraform-aws-modules/rds-aurora/aws"
  version = "7.7.1"

  name = "${var.environment_name}-catalog"
  engine = "aurora-mysql"
  engine_version = "8.0"
  instance_class = "db.t3.medium"
  allow_major_version_upgrade = true

  instances = {
    one = {}
  }

  vpc_id = var.vpc_id
  subnets = var.subnet_ids

  allowed_security_groups = concat(var.allowed_security_group_ids, [var.catalog_security_group_id])

  master_password        = random_string.catalog_db_master.result
  create_random_password = false
  database_name          = "catalog"
  storage_encrypted      = true
  apply_immediately      = true
  skip_final_snapshot    = true

  create_db_parameter_group = true
  db_parameter_group_name   = "${var.environment_name}-catalog"
  db_parameter_group_family = "aurora-mysql8.0"

  create_db_cluster_parameter_group = true
  db_cluster_parameter_group_name   = "${var.environment_name}-catalog"
  db_cluster_parameter_group_family = "aurora-mysql8.0"

  tags             = var.tags
}

resource "random_string" "catalog_db_master" {
  length  = 10
  special = false
}