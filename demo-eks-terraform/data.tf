###############################################################
#
# This file contains configuration for all data source queries
#
###############################################################

# Get the default VPC details
data "aws_vpc" "default_vpc" {
  default = true
}

# Get the subnets to use for the cluster to bind to and the autoscaling group
# to place nodes in.
data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default_vpc.id]
  }
  filter {
    name = "availability-zone"
    values = [
      "${var.aws_region}a",
      "${var.aws_region}b",
      "${var.aws_region}c",
    ]
  }
}

# Get AMI ID for latest recommended Amazon Linux 2 image
data "aws_ssm_parameter" "node_ami" {
  name = "/aws/service/eks/optimized-ami/1.31/amazon-linux-2/recommended/image_id"
}

# Current account ID — used to build the cluster role ARN as a stable literal
# so the EKS cluster isn't marked for replacement whenever the role's module
# instance is re-created (the role name is fixed, so the ARN is deterministic).
data "aws_caller_identity" "current" {}
