# This module is conditionally invoked to get the EKS cluster service role
# if it is already present. In some EKS course labs, it is present.

variable "cluster_role_name" {
  type        = string
  description = "Name of the cluster role"
  default     = "eksClusterRole"
}

data "aws_iam_role" "eksClusterRole" {
    name = var.cluster_role_name
}

output "eksClusterRole_arn" {
  value = data.aws_iam_role.eksClusterRole.arn
}
