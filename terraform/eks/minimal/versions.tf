terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.37.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.17.0"
    }
  }
}

provider "aws" {
}

provider "kubernetes" {
  host                   = module.retail_app_eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.retail_app_eks.cluster_certificate_authority_data)
  token                  = data.aws_eks_cluster_auth.this.token
}

provider "kubernetes" {
  alias = "cluster"

  host                   = module.retail_app_eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.retail_app_eks.cluster_certificate_authority_data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}

provider "helm" {
  kubernetes {
    host                   = module.retail_app_eks.cluster_endpoint
    token                  = data.aws_eks_cluster_auth.this.token
    cluster_ca_certificate = base64decode(module.retail_app_eks.cluster_certificate_authority_data)
  }
}
