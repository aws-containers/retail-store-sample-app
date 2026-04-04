# ─── ECR Repositories ────────────────────────────────────────────────────────
# Creates one private ECR repository per microservice.
# Naming convention: <project_name>/<service>

resource "aws_ecr_repository" "this" {
  for_each = toset(var.services)

  name                 = "${var.project_name}/${each.key}"
  image_tag_mutability = var.image_tag_mutability

  image_scanning_configuration {
    scan_on_push = var.scan_on_push
  }

  # AES256 server-side encryption (default for ECR, explicit for audit trail)
  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

# Lifecycle Policies
# Rule 1 – Retain the last N tagged images with the "v" prefix (release images).
# Rule 2 – Expire untagged/intermediate images after N days to reduce storage costs.

resource "aws_ecr_lifecycle_policy" "this" {
  for_each = aws_ecr_repository.this

  repository = each.value.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep the last ${var.tagged_keep_count} versioned images (v* tags)"
        selection = {
          tagStatus     = "tagged"
          tagPrefixList = ["v"]
          countType     = "imageCountMoreThan"
          countNumber   = var.tagged_keep_count
        }
        action = { type = "expire" }
      },
      {
        rulePriority = 2
        description  = "Expire untagged images older than ${var.untagged_expire_days} days"
        selection = {
          tagStatus   = "untagged"
          countType   = "sinceImagePushed"
          countUnit   = "days"
          countNumber = var.untagged_expire_days
        }
        action = { type = "expire" }
      }
    ]
  })
}
