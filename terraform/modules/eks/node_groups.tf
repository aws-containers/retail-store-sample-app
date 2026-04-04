# ─── Managed Node Groups ─────────────────────────────────────────────────────
# Two node groups, each pinned to one private subnet in a distinct AZ.
# This guarantees HA: if one AZ fails, the other group continues serving traffic.
#
# Cluster Autoscaler tags are set on each ASG so the autoscaler can discover
# and scale them automatically.

locals {
  eks_managed_node_groups = {

    # Worker nodes in Availability Zone 1
    ng_az1 = {
      name           = "${var.cluster_name}-ng-az1"
      instance_types = [var.node_instance_type]

      # Pin this node group to the first private subnet (AZ 1)
      subnet_ids = [var.private_subnet_ids[0]]

      min_size     = var.node_min_size
      max_size     = var.node_max_size
      desired_size = var.node_desired_size

      # Node OS – Amazon Linux 2023 is the default for EKS 1.30+
      ami_type = "AL2023_x86_64_STANDARD"

      labels = {
        role = "application"
        az   = "az1"
      }

      # Cluster Autoscaler auto-discovery tags (applied to the underlying ASG)
      tags = {
        "k8s.io/cluster-autoscaler/enabled"             = "true"
        "k8s.io/cluster-autoscaler/${var.cluster_name}" = "owned"
      }
    }

    # Worker nodes in Availability Zone 2
    ng_az2 = {
      name           = "${var.cluster_name}-ng-az2"
      instance_types = [var.node_instance_type]

      # Pin this node group to the second private subnet (AZ 2)
      subnet_ids = [var.private_subnet_ids[1]]

      min_size     = var.node_min_size
      max_size     = var.node_max_size
      desired_size = var.node_desired_size

      ami_type = "AL2023_x86_64_STANDARD"

      labels = {
        role = "application"
        az   = "az2"
      }

      tags = {
        "k8s.io/cluster-autoscaler/enabled"             = "true"
        "k8s.io/cluster-autoscaler/${var.cluster_name}" = "owned"
      }
    }
  }
}
