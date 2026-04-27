resource "random_string" "catalog_opensearch_master" {
  length           = 10
  special          = true
  upper            = true
  lower            = true
  numeric          = true
  min_special      = 1
  min_upper        = 1
  min_lower        = 1
  min_numeric      = 1
  override_special = "#"
}

resource "aws_security_group" "catalog_opensearch_sg" {
  name        = "catalog_opensearch_sg"
  description = "Security group for OpenSearch"
  vpc_id      = var.vpc_id
}

resource "aws_security_group_rule" "allow_https_from_client" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  security_group_id        = aws_security_group.catalog_opensearch_sg.id
  source_security_group_id = var.catalog_security_group_id
  description              = "Allow HTTPS from client"
}


module "catalog_opensearch" {
  source  = "terraform-aws-modules/opensearch/aws"
  version = "1.5.0"

  count  = (var.catalog_search_enabled && var.catalog_search_provider == "aws") ? 1 : 0

  domain_name    = "${var.environment_name}-catalog"
  engine_version = "OpenSearch_3.3"

  cluster_config = {
    instance_type            = "r8g.medium.search"
    instance_count           = 1
    zone_awareness_enabled   = false
    dedicated_master_enabled = false
  }

  ebs_options = {
    ebs_enabled = true
    volume_size = 50
    volume_type = "gp3"
  }

  vpc_options = {
    subnet_ids         = [var.subnet_ids[0]]
    security_group_ids = [aws_security_group.catalog_opensearch_sg.id]
  }

  advanced_security_options = {
    enabled                        = true
    internal_user_database_enabled = true
    master_user_options = {
      master_user_name     = var.catalog_search_username
      master_user_password = random_string.catalog_opensearch_master.result
    }
  }

  encrypt_at_rest = {
    enabled = true
  }

  node_to_node_encryption = {
    enabled = true
  }

  domain_endpoint_options = {
    enforce_https       = true
    tls_security_policy = "Policy-Min-TLS-1-2-2019-07"
  }

  create_access_policy = false
  access_policies = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "es:*"
        Resource  = "arn:aws:es:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:domain/${var.environment_name}-catalog/*"
      }
    ]
  })

  create_security_group        = false
  create_cloudwatch_log_groups = false
  log_publishing_options       = []
  auto_tune_options            = {}
  off_peak_window_options      = {}
  software_update_options      = {}

  tags = var.tags
}