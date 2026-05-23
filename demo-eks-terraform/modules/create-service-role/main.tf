# This module is conditionally invoked to create the EKS cluster service role
# if it is not already present. In some EKS course labs, it is present.

variable "cluster_role_name" {
  type        = string
  description = "Name of the cluster role"
  default     = "eksClusterRole"
}

variable "additional_policy_arns" {
  type        = list(string)
  description = "ARNs of policies to attach to role"
  default     = []
}

data "aws_iam_policy_document" "assume_role_eks" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["eks.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "eksClusterRole" {
  name               = var.cluster_role_name
  assume_role_policy = data.aws_iam_policy_document.assume_role_eks.json
}

resource "aws_iam_role_policy_attachment" "eksClusterRole_AmazonEKSClusterPolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eksClusterRole.name
}

resource "aws_iam_role_policy_attachment" "eksClusterRole_additional_policies" {
  for_each = {
    for index, arn in var.additional_policy_arns : index => arn
  }
  policy_arn = each.value
  role       = aws_iam_role.eksClusterRole.name
}

# Optionally, enable Security Groups for Pods
# Reference: https://docs.aws.amazon.com/eks/latest/userguide/security-groups-for-pods.html
resource "aws_iam_role_policy_attachment" "eksClusterRole_AmazonEKSVPCResourceController" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
  role       = aws_iam_role.eksClusterRole.name
}

output "eksClusterRole_arn" {
  value = aws_iam_role.eksClusterRole.arn
}