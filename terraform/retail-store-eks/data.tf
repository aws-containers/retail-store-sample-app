data "aws_caller_identity" "current" {}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
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

# Latest Amazon Linux 2 EKS-optimised AMI for Kubernetes 1.31
data "aws_ssm_parameter" "node_ami" {
  name = "/aws/service/eks/optimized-ami/1.31/amazon-linux-2/recommended/image_id"
}
