variable "aws_region" {
  description = "AWS region where all resources will be deployed"
  type        = string
  default     = "ap-southeast-1"
}

variable "project_name" {
  description = "Short name for this project - used as a prefix in resource names"
  type        = string
  default     = "e-comm"
}

variable "environment" {
  description = "Deployment environment label (dev | staging | prod)"
  type        = string
  default     = "dev"
}

# Networking
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "az_ids" {
  type        = list(string)
  description = "List of availability zone IDs for subnets"
  default     = ["apse1-az1", "apse1-az2"]
}
# EKS
variable "eks_cluster_version" {
  description = "Kubernetes version to deploy on EKS"
  type        = string
  default     = "1.34"
}

variable "node_instance_type" {
  description = "EC2 instance type for worker nodes"
  type        = string
  default     = "t3.micro"
}

variable "node_min_size" {
  description = "Minimum nodes per node group"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum nodes per node group (Cluster Autoscaler ceiling)"
  type        = number
  default     = 6
}

variable "node_desired_size" {
  description = "Initial desired node count per node group"
  type        = number
  default     = 2
}

# ECR
variable "ecr_services" {
  description = "Microservices that require a dedicated ECR repository"
  type        = list(string)
  default     = ["ui", "catalog", "cart", "checkout", "orders", "load-generator"]
}

# Tags
variable "tags" {
  description = "Additional tags merged with the auto-generated common tags"
  type        = map(string)
  default     = {}
}

# Root IRSA (optional)
variable "create_root_oidc_iam_role" {
  description = "Whether to create an OIDC IAM role from the root module."
  type        = bool
  default     = false
}

variable "root_oidc_iam_role_name" {
  description = "IAM role name for the optional root-level IRSA role."
  type        = string
  default     = ""
}

variable "root_oidc_namespace_service_accounts" {
  description = "List of namespace:serviceaccount values allowed for the optional root-level IRSA role."
  type        = list(string)
  default     = []
}

# ArgoCD variables
variable "argocd_chart_version" {
  description = "Version of the argo-cd Helm chart to install"
  type        = string
  default     = "6.0.0"
}

variable "argocd_create_namespace" {
  description = "Whether to create the argocd namespace if it doesn't exist"
  type        = bool
  default     = true
}

variable "argocd_namespace" {
  description = "Kubernetes namespace where ArgoCD will be deployed"
  type        = string
  default     = "argocd"
}

variable "gitops_repository_url" {
  description = "URL of the GitOps repository (e.g., https://github.com/org/ecommerce-gitops.git)"
  type        = string
  default     = ""
}

variable "gitops_repository_branch" {
  description = "Default branch of the GitOps repository"
  type        = string
  default     = "main"
}

variable "gitops_pat" {
  description = "GitHub Personal Access Token for ArgoCD to access private GitOps repository"
  type        = string
  sensitive   = true
  default     = ""
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

variable "argocd_server_admin_password_hash" {
  description = "bcrypt hash of ArgoCD server admin password (leave empty to disable password auth)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "argocd_helm_values_override" {
  description = "Additional Helm values to override ArgoCD default configuration (as HCL map)"
  type        = map(any)
  default     = {}
}

variable "enable_argocd_repo_server_irsa" {
  description = "Whether to enable IRSA for ArgoCD repo server to access ECR"
  type        = bool
  default     = true
}

variable "argocd_repo_server_ecr_read_policy" {
  description = "Whether to attach ECR read policy to ArgoCD repo server IRSA role"
  type        = bool
  default     = true
}
