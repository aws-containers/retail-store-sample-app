terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
    time = {
      source  = "hashicorp/time"
      version = "~> 0.9"
    }
  }

  # Partial backend — all values supplied at `terraform init` time via -backend-config flags.
  # This makes the module portable across AWS accounts without code changes.
  backend "s3" {}
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "retail-store-sample-app"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

provider "kubernetes" {
  host                   = aws_eks_cluster.retail_store.endpoint
  cluster_ca_certificate = base64decode(aws_eks_cluster.retail_store.certificate_authority[0].data)

  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args = [
      "eks", "get-token",
      "--cluster-name", aws_eks_cluster.retail_store.name,
      "--region", var.aws_region,
    ]
  }
}

provider "helm" {
  kubernetes {
    host                   = aws_eks_cluster.retail_store.endpoint
    cluster_ca_certificate = base64decode(aws_eks_cluster.retail_store.certificate_authority[0].data)

    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args = [
        "eks", "get-token",
        "--cluster-name", aws_eks_cluster.retail_store.name,
        "--region", var.aws_region,
      ]
    }
  }
}

locals {
  account_id      = data.aws_caller_identity.current.account_id
  ecr_registry    = "${local.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
  tf_state_bucket = "retail-store-tfstate-${local.account_id}"
}
