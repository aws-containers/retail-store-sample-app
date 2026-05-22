####################################################################
# Private ECR repositories
#
# Application services — built from source in this repo:
#   ui, catalog, cart, checkout, orders
#
# Dependency images — mirrored from public sources:
#   mysql, postgres, rabbitmq, dynamodb-local, redis
####################################################################

locals {
  service_repos = toset([
    "retail-store-sample-ui",
    "retail-store-sample-catalog",
    "retail-store-sample-cart",
    "retail-store-sample-checkout",
    "retail-store-sample-orders",
  ])

  mirror_repos = toset([
    "retail-store-sample-mysql",
    "retail-store-sample-postgres",
    "retail-store-sample-rabbitmq",
    "retail-store-sample-dynamodb-local",
    "retail-store-sample-redis",
  ])

  all_repos = setunion(local.service_repos, local.mirror_repos)
}

resource "aws_ecr_repository" "services" {
  for_each = local.all_repos

  name                 = each.key
  image_tag_mutability = "MUTABLE"
  force_delete         = false

  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }
}

# Lifecycle policy: keep the 30 most recent images, expire untagged after 7 days
resource "aws_ecr_lifecycle_policy" "services" {
  for_each   = aws_ecr_repository.services
  repository = each.value.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Expire untagged images after 7 days"
        selection = {
          tagStatus   = "untagged"
          countType   = "sinceImagePushed"
          countUnit   = "days"
          countNumber = 7
        }
        action = { type = "expire" }
      },
      {
        rulePriority = 2
        description  = "Keep only the 30 most recent tagged images"
        selection = {
          tagStatus     = "tagged"
          tagPrefixList = ["v", "latest", "sha-"]
          countType     = "imageCountMoreThan"
          countNumber   = 30
        }
        action = { type = "expire" }
      }
    ]
  })
}

# Repository policy: allow the EKS node role to pull images
resource "aws_ecr_repository_policy" "node_pull" {
  for_each   = aws_ecr_repository.services
  repository = each.value.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowNodePull"
        Effect = "Allow"
        Principal = {
          AWS = aws_iam_role.node_instance_role.arn
        }
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
        ]
      },
      {
        Sid    = "AllowGitHubActionsPush"
        Effect = "Allow"
        Principal = {
          AWS = aws_iam_role.github_actions.arn
        }
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:DescribeRepositories",
          "ecr:ListImages",
        ]
      }
    ]
  })
}
