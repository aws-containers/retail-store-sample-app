# Previously held an inline aws_iam_role_policy on the EKS cluster role.
# Removed: iam:PutRolePolicy is denied for this lab's IAM user, and the
# IRSA-attached ALB controller policy (alb-controller.tf) covers the
# permissions the in-cluster controllers actually need.
