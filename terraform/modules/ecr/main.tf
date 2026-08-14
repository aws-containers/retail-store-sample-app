resource "aws_ecr_repository" "main" {
  for_each = var.repo_name

  name                 = each.value
  image_tag_mutability = "IMMUTABLE"
  force_delete         = false


  image_scanning_configuration {
    scan_on_push = true
  }

  encryption_configuration {
    encryption_type = "AES256"
  }


  tags = {
    Name        = each.value
    Project     = var.project_name
    Environment = var.env
  }
}

resource "aws_ecr_lifecycle_policy" "main" {
  for_each   = aws_ecr_repository.main
  repository = each.value.name
  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Delete untagged images after configured number of days"
        selection = {
          tagStatus   = "untagged"
          countType   = "sinceImagePushed"
          countUnit   = "days"
          countNumber = var.untagged_image_expiration_days
        }
        action = {
          type = "expire"
        }
      },
      {
        rulePriority = 2
        description  = "Keep only the configured maximum number of images"
        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = var.maximum_image_count
        }

        action = {
          type = "expire"
        }
      }
    ]
  })
}