module "tags" {
  source = "../lib/tags"

  environment_name = var.environment_name
}

module "vpc" {
  source = "../lib/vpc"

  environment_name = var.environment_name

  public_subnet_tags = {
    "kubernetes.io/cluster/${var.environment_name}" = "shared"
    "kubernetes.io/role/elb"                        = 1
  }

  private_subnet_tags = {
    "kubernetes.io/cluster/${var.environment_name}" = "shared"
    "kubernetes.io/role/internal-elb"               = 1
  }

  tags = module.tags.result
}

module "dependencies" {
  source = "../lib/dependencies"

  environment_name = var.environment_name
  tags             = module.tags.result

  vpc_id             = module.vpc.inner.vpc_id
  subnet_ids         = module.vpc.inner.private_subnets
  availability_zones = module.vpc.inner.azs
}

module "retail_app_eks" {
  source = "../lib/eks"

  environment_name = var.environment_name
  cluster_version  = "1.24"
  vpc_id           = module.vpc.inner.vpc_id
  subnet_ids       = module.vpc.inner.private_subnets
  tags             = module.tags.result
}

# Setup deploying role cluster with auth
resource "null_resource" "setup_cluster_access" {
  provisioner "local-exec" {
    interpreter = ["bash", "-exc"]
    command     = "aws eks --region ${data.aws_region.current.name} update-kubeconfig --name ${module.retail_app_eks.eks_cluster_id}"
  }

  depends_on = [
    module.retail_app_eks
  ]
}

# Apply YAML kubernetes-manifest configurations
resource "null_resource" "apply_deployment" {
  provisioner "local-exec" {
    interpreter = ["bash", "-exc"]
    command     = "kubectl apply -k ${var.filepath_manifest}"
  }

  depends_on = [
    module.retail_app_eks,
    resource.null_resource.setup_cluster_access
  ]
}
