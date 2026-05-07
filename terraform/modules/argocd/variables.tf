variable "cluster_name" {
  description = "Name of the Kubernetes cluster (used for naming/annotations)"
  type        = string
}

variable "gitops_repo_url" {
  description = "URL of the GitOps repository (e.g., https://github.com/org/ecommerce-gitops.git)"
  type        = string
}

variable "gitops_repo_branch" {
  description = "Default branch of the GitOps repository"
  type        = string
  default     = "main"
}

variable "gitops_pat" {
  description = "GitHub Personal Access Token for ArgoCD to access private GitOps repository"
  type        = string
  sensitive   = true
}

variable "gitops_username" {
  description = "GitHub username for repository authentication"
  type        = string
  default     = "ecommerce-cd-bot"
}

variable "ecr_repository_prefix" {
  description = "ECR repository prefix used by ArgoCD for image lookups (e.g., 123456789.dkr.ecr.ap-southeast-1.amazonaws.com)"
  type        = string
  default     = ""
}

variable "argocd_chart_version" {
  description = "Version of the argo-cd Helm chart to install"
  type        = string
  default     = "6.0.0"
}

variable "argocd_namespace" {
  description = "Kubernetes namespace where ArgoCD will be deployed"
  type        = string
  default     = "argocd"
}

variable "argocd_create_namespace" {
  description = "Whether to create the argocd namespace if it doesn't exist"
  type        = bool
  default     = true
}

variable "argocd_helm_values_override" {
  description = "Additional Helm values to override ArgoCD default configuration (as HCL map)"
  type        = map(any)
  default     = {}
}

variable "enable_repo_server_irsa_annotation" {
  description = "Whether to set repo server IRSA annotation from provided role ARN"
  type        = bool
  default     = true
}

variable "repo_server_iam_role_arn" {
  description = "IAM role ARN to annotate on argocd-repo-server ServiceAccount for IRSA"
  type        = string
  default     = ""
}

variable "enable_ecr_integration" {
  description = "Whether to configure an ECR OCI registry inside ArgoCD"
  type        = bool
  default     = false
}
