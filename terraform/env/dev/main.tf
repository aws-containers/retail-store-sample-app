module "vpc" {
  source = "../../modules/vpc"

  vpc_cidr       = var.vpc_cidr
  project_name   = var.project_name
  environment    = var.env
  private_subnet = var.private_subnets
  public_subnet  = var.public_subnets
}


module "ecr" {
  source = "../../modules/ecr"

  repo_name    = var.ecr_repository_names
  project_name = var.project_name
  env          = var.env
}


module "github_oidc" {
  source = "../../modules/iam/github-oidc"
}

module "github_ecr_role" {
  source = "../../modules/iam/github-ecr-role"

  project_name        = var.project_name
  environment         = var.env
  github_repo         = var.github_repository
  github_branch       = var.github_branch
  oidc_provider_arn   = module.github_oidc.provider_arn
  ecr_repository_arns = toset(values(module.ecr.repository_arns))
}