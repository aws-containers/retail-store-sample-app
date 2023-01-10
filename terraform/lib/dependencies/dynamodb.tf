module "dynamodb-carts" {
  source = "terraform-aws-modules/dynamodb-table/aws"

  name     = "${var.environment_name}-carts"
  hash_key = "id"

  attributes = [
    {
      name = "id"
      type = "S"
    },
    {
      name = "customerId"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "idx_global_customerId"
      hash_key        = "customerId"
      projection_type = "ALL"
    }
  ]

  tags = var.tags
}

resource "aws_iam_policy" "carts_dynamo" {
  name        = "${var.environment_name}-carts-dynamo"
  path        = "/"
  description = "Dynamo policy for carts application"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllAPIActionsOnCart",
      "Effect": "Allow",
      "Action": "dynamodb:*",
      "Resource": [
        "arn:${local.aws_partition}:dynamodb:${local.aws_region}:${local.aws_account_id}:table/${module.dynamodb-carts.dynamodb_table_id}",
        "arn:${local.aws_partition}:dynamodb:${local.aws_region}:${local.aws_account_id}:table/${module.dynamodb-carts.dynamodb_table_id}/index/*"
      ]
    }
  ]
}
EOF
  tags   = var.tags
}