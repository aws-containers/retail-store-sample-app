####################################################################
#
# aws-auth ConfigMap: maps the node IAM role to Kubernetes RBAC
# so EC2 worker nodes (from the autoscaling group) can register
# with the cluster as `system:nodes`.
#
# Cluster creator already has admin via
# bootstrap_cluster_creator_admin_permissions = true in eks.tf,
# so we only need to map the node role here.
#
####################################################################

resource "kubernetes_config_map" "aws_auth" {
  metadata {
    name      = "aws-auth"
    namespace = "kube-system"
  }

  data = {
    mapRoles = yamlencode([
      {
        rolearn  = aws_iam_role.node_instance_role.arn
        username = "system:node:{{EC2PrivateDNSName}}"
        groups = [
          "system:bootstrappers",
          "system:nodes",
        ]
      },
    ])
  }

  depends_on = [
    aws_eks_cluster.demo_eks,
    aws_cloudformation_stack.autoscaling_group,
  ]
}
