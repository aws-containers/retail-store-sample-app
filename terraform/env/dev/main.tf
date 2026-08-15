module "vpc" {
  source = "../../modules/vpc"

  vpc_cidr       = var.vpc_cidr
  project_name   = var.project_name
  environment    = var.env
  private_subnet = var.private_subnets
  public_subnet  = var.public_subnets
}
