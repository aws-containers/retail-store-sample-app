# Inline policy on the EKS cluster role.
# Using aws_iam_role_policy (inline) instead of aws_iam_policy (managed)
# avoids iam:TagPolicy which KodeKloud lab users don't have.
resource "aws_iam_role_policy" "loadbalancer_policy" {
  count = var.use_predefined_role ? 0 : 1
  name  = var.additional_policy_name
  role  = var.cluster_role_name

  policy = jsonencode(yamldecode(file("./policy.yaml")))

  depends_on = [module.create_eksClusterRole]
}
