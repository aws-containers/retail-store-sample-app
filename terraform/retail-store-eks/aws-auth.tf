####################################################################
# aws-auth ConfigMap
# Maps node IAM role + GitHub Actions IAM role to Kubernetes RBAC
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
        groups   = ["system:bootstrappers", "system:nodes"]
      },
      {
        rolearn  = aws_iam_role.github_actions.arn
        username = "github-actions"
        groups   = ["system:masters"]
      },
    ])
  }

  depends_on = [
    aws_eks_cluster.retail_store,
    aws_cloudformation_stack.node_asg,
  ]
}
