variable "environment_name" {
  type    = string
  default = "retail-store"
}

variable "filepath_manifest" {
  type        = string
  description = "Path to Containers Retail Sample's Kubernetes resources, written using Kustomize"
  default     = "../../deploy/kubernetes/kustomize/base/"
}
