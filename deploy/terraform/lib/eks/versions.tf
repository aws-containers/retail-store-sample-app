terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.48"
    }
    kubernetes = {
      source                = "hashicorp/kubernetes"
      version               = ">= 2.10"
      configuration_aliases = [kubernetes.cluster, kubernetes.addons]
    }
    helm = {
      source  = "hashicorp/helm"
      version = ">= 2.4.1"
    }
  }
}
