# Orchestrates ArgoCD deployment using the existing oidc_iam_role module
# - Creates ECR read policy
# - Creates an OIDC IAM role (IRSA) using ./modules/oidc_iam_role
# - Attaches the ECR read policy to the created role
# - Calls modules/argocd to install ArgoCD and create repo secrets

# Get current AWS caller identity
data "aws_caller_identity" "current" {}

# ECR read policy for ArgoCD repo server
resource "aws_iam_policy" "argocd_ecr_read" {
  count       = var.enable_argocd_repo_server_irsa && var.argocd_repo_server_ecr_read_policy ? 1 : 0
  name        = "${local.cluster_name}-argocd-ecr-read"
  description = "Policy for ArgoCD repo server to read images from ECR"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "ECRAuthToken"
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      },
      {
        Sid    = "ECRBatchLayerOperations"
        Effect = "Allow"
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage"
        ]
        Resource = "arn:aws:ecr:${var.aws_region}:${data.aws_caller_identity.current.account_id}:repository/*"
      },
      {
        Sid    = "ECRDescribeRepositories"
        Effect = "Allow"
        Action = [
          "ecr:DescribeRepositories",
          "ecr:ListImages"
        ]
        Resource = "arn:aws:ecr:${var.aws_region}:${data.aws_caller_identity.current.account_id}:repository/*"
      }
    ]
  })

  tags = local.common_tags
}

# Create IRSA role for ArgoCD repo server using teammate's module
module "argocd_repo_server_irsa" {
  count  = var.enable_argocd_repo_server_irsa ? 1 : 0
  source = "./modules/oidc_iam_role"

  role_name                  = "${local.cluster_name}-argocd-repo-server"
  oidc_provider_arn          = module.eks.oidc_provider_arn
  namespace_service_accounts = ["${var.argocd_namespace}:argocd-repo-server"]

  tags = local.common_tags
}

# Attach the ECR read policy to the IRSA role (if created)
resource "aws_iam_role_policy_attachment" "argocd_ecr_read_attach" {
  count      = var.enable_argocd_repo_server_irsa && var.argocd_repo_server_ecr_read_policy ? 1 : 0
  role       = module.argocd_repo_server_irsa[0].iam_role_name
  policy_arn = aws_iam_policy.argocd_ecr_read[0].arn
}

# Finally, call the argocd module
module "argocd" {
  source = "./modules/argocd"

  cluster_name                       = local.cluster_name
  gitops_repo_url                    = var.gitops_repository_url
  gitops_repo_branch                 = var.gitops_repository_branch
  gitops_pat                         = var.gitops_pat
  gitops_username                    = var.gitops_username
  ecr_repository_prefix              = var.ecr_repository_prefix
  argocd_chart_version               = var.argocd_chart_version
  argocd_namespace                   = var.argocd_namespace
  argocd_create_namespace            = var.argocd_create_namespace
  argocd_helm_values_override        = var.argocd_helm_values_override
  enable_repo_server_irsa_annotation = var.enable_argocd_repo_server_irsa
  enable_ecr_integration             = var.ecr_repository_prefix != ""

  # pass the IAM role ARN if IRSA was created
  repo_server_iam_role_arn = var.enable_argocd_repo_server_irsa ? module.argocd_repo_server_irsa[0].iam_role_arn : ""

  depends_on = [module.eks]
}
