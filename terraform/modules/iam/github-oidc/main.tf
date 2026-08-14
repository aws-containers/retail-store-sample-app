resource "aws_iam_openid_connect_provider" "github" {
  url = "https://token.actions.githubusercontent.com"
  client_id_list = [
    "sts.amazonaws.com"
  ]
  tags = {
    Name      = "github-actions-oidc"
    ManagedBy = "Terraform"
  }
}

