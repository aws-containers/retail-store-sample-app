module "dynamodb-carts" {
    source = "terraform-aws-modules/dynamodb-table/aws"

    name             = "${var.environment_name}-carts"
    hash_key         = "id"

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
    
    tags             = module.tags.result
}

module "ddb_tags" {
  source = "../tags"

  environment_name = var.environment_name
}