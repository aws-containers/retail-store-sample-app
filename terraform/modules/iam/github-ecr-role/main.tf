data "aws_iam_policy_document" "github_trust" {
  statement {
    sid    = "GitHubOIDCTrust"
    effect = "Allow"
    actions = [
      "sts:AssumeRoleWithWebIdentity"
    ]
    principals {
      type        = "Federated"
      identifiers = [var.oidc_provider_arn]
    }
    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }
    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:${var.github_repo}:ref:refs/heads/${var.github_branch}"]
    }

  }
}

resource "aws_iam_role" "github_ecr_role" {
  name               = "${var.project_name}-${var.environment}-github-ecr-role"
  assume_role_policy = data.aws_iam_policy_document.github_trust.json
  tags = {
    Name        = "${var.project_name}-${var.environment}-github-ecr-role"
    Project     = var.project_name
    Environment = var.environment
    Purpose     = "GitHubActionsECR"
    managed_by  = "terraform"
  }
}

data "aws_iam_policy_document" "ecr_access" {
  statement {
    sid    = "ECRAccess"
    effect = "Allow"
    actions = [
      "ecr:GetAuthorizationToken",
    ]
    resources = [
      "*"
    ]
  }
  statement {
    sid    = "ECRRepositoryAccess"
    effect = "Allow"
    actions = [
      "ecr:BatchCheckLayerAvailability",
      "ecr:BatchGetImage",
      "ecr:CompleteLayerUpload",
      "ecr:DescribeImages",
      "ecr:DescribeRepositories",
      "ecr:GetDownloadUrlForLayer",
      "ecr:InitiateLayerUpload",
      "ecr:ListImages",
      "ecr:PutImage",
      "ecr:UploadLayerPart"
    ]
    resources = var.ecr_repository_arns
  }
}

resource "aws_iam_role_policy" "ecr_access" {
  name   = "${var.project_name}-${var.environment}-github-ecr-policy"
  role   = aws_iam_role.github_ecr_role.name
  policy = data.aws_iam_policy_document.ecr_access.json
}

