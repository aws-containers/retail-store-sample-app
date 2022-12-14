module "catalog_rds" {
  source  = "terraform-aws-modules/rds-aurora/aws"

  name = "${var.environment_name}-catalog"
  engine = "aurora-mysql"
  engine_version = "5.7"
  instance_class = "db.t3.small"

  instances = {
    one = {}
  }

  vpc_id = module.vpc.inner.vpc_id
  subnets = module.vpc.inner.private_subnets

  allowed_security_groups = [aws_security_group.catalog_rds_ingress.id]

  master_password        = random_string.catalog_db_master.result
  create_random_password = false
  database_name          = "catalog"
  storage_encrypted      = true
  apply_immediately      = true
  skip_final_snapshot    = true

  create_db_parameter_group = true
  db_parameter_group_name   = "${var.environment_name}-catalog"
  db_parameter_group_family = "aurora-mysql5.7"

  create_db_cluster_parameter_group = true
  db_cluster_parameter_group_name   = "${var.environment_name}-catalog"
  db_cluster_parameter_group_family = "aurora-mysql5.7"

  tags             = module.tags.result
}

resource "random_string" "catalog_db_master" {
  length  = 10
  special = false
}

resource "aws_security_group" "catalog_rds_ingress" {
  name        = "${var.environment_name}-catalog-db"
  description = "Allow inbound traffic to catalog MySQL"
  vpc_id      = module.vpc.inner.vpc_id

  tags             = module.tags.result
}

module "rds_vpc" {
  source = "../vpc"

  environment_name = var.environment_name
  tags             = module.tags.result
}

module "rds_tags" {
  source = "../tags"

  environment_name = var.environment_name
}