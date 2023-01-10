variable "environment_name" {
  type    = string
  default = "retail-store"
}

variable "kustomization_path" {
  type        = string
  description = "Path to kustomization that will deploy the sample application in the EKS cluster"
  default     = "../../deploy/kubernetes/kustomize/recipes/full"
}
