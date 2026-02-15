################################################################################
# 4.3 Secure Developer Access
################################################################################

# Create the IAM User
resource "aws_iam_user" "dev_view" {
  name = "bedrock-dev-view"
  path = "/"

  tags = {
    Project = "Bedrock"
  }
}

# 1. AWS Console Access: Attach ReadOnlyAccess
resource "aws_iam_user_policy_attachment" "dev_view_readonly" {
  user       = aws_iam_user.dev_view.name
  policy_arn = "arn:aws:iam::aws:policy/ReadOnlyAccess"
}

# Create Access Keys for the user (to be outputted)
resource "aws_iam_access_key" "dev_view_key" {
  user = aws_iam_user.dev_view.name
}

# Note: Kubernetes Access (RBAC) is handled in the EKS module in main.tf 
# using the 'access_entries' variable to map this user to AmazonEKSViewPolicy.
