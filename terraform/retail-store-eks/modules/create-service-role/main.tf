variable "cluster_role_name" {
  type    = string
  default = "retailStoreEksClusterRole"
}

variable "additional_policy_arns" {
  type    = list(string)
  default = []
}

data "aws_iam_policy_document" "assume_eks" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["eks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "eksClusterRole" {
  name               = var.cluster_role_name
  assume_role_policy = data.aws_iam_policy_document.assume_eks.json
}

resource "aws_iam_role_policy_attachment" "cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eksClusterRole.name
}

resource "aws_iam_role_policy_attachment" "vpc_controller" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
  role       = aws_iam_role.eksClusterRole.name
}

resource "aws_iam_role_policy_attachment" "additional" {
  for_each = {
    for i, arn in var.additional_policy_arns : i => arn
  }
  policy_arn = each.value
  role       = aws_iam_role.eksClusterRole.name
}

output "eksClusterRole_arn" {
  value = aws_iam_role.eksClusterRole.arn
}
