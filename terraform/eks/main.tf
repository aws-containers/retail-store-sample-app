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

  catalog_security_group_id  = aws_security_group.catalog.id
  orders_security_group_id   = aws_security_group.orders.id
  checkout_security_group_id = aws_security_group.checkout.id
}

module "retail_app_eks" {
  source = "../lib/eks"

  environment_name = var.environment_name
  cluster_version  = "1.24"
  vpc_id           = module.vpc.inner.vpc_id
  vpc_cidr         = module.vpc.inner.vpc_cidr_block
  subnet_ids       = module.vpc.inner.private_subnets
  tags             = module.tags.result
}

locals {
  kubeconfig = yamlencode({
    apiVersion      = "v1"
    kind            = "Config"
    current-context = "terraform"
    clusters = [{
      name = module.retail_app_eks.eks_cluster_id
      cluster = {
        certificate-authority-data = module.retail_app_eks.cluster_object.eks_cluster_certificate_authority_data
        server                     = module.retail_app_eks.cluster_endpoint
      }
    }]
    contexts = [{
      name = "terraform"
      context = {
        cluster = module.retail_app_eks.eks_cluster_id
        user    = "terraform"
      }
    }]
    users = [{
      name = "terraform"
      user = {
        token = data.aws_eks_cluster_auth.this.token
      }
    }]
  })
}

resource "null_resource" "apply_deployment" {
  provisioner "local-exec" {
    interpreter = ["bash", "-exc"]
    environment = {
      KUBECONFIG = base64encode(local.kubeconfig)

      CARTS_IAM_ROLE = module.iam_assumable_role_carts.iam_role_arn
      CHECKOUT_SG_ID = aws_security_group.checkout.id
      ORDERS_SG_ID   = aws_security_group.orders.id
      CATALOG_SG_ID  = aws_security_group.catalog.id
    }
    command = "kubectl apply -l is-namespace!=yes -k ${var.kustomization_path} --kubeconfig <(echo $KUBECONFIG | base64 -d)"
  }
}
