module "vpc" {
  source = "../../modules/vpc"

  vpc_cidr       = var.vpc_cidr
  project_name   = var.project_name
  environment    = var.env
  private_subnet = var.private_subnets
  public_subnet  = var.public_subnets
}

module "ecr_repo" {
  source       = "../../modules/ecr"
  repo_name    = var.ecr_repository_names
  project_name = var.project_name
  env          = var.env

  untagged_image_expiration_days = 7
  maximum_image_count            = 20
}
module "github_oidc" {
  source = "../../modules/iam/github-oidc"
}

module "github_ecr_role" {
  source        = "../../modules/iam/github-ecr-role"
  github_branch = "develop"


  project_name = var.project_name
  environment  = var.env
  github_repo  = var.github_repository
  ecr_repository_arns = values(
    module.ecr_repo.repository_arns
  )

  oidc_provider_arn = var.github_oidc_provider_arn
}