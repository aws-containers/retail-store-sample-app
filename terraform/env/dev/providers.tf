provider "aws" {
  region = var.aws_region
  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.env
      ManagedBy   = "Terraform"
      Owner       = var.owner
      costcenter  = "100"
    }
  }
}
