# ─── AWS Load Balancer Controller ────────────────────────────────────────────
# Watches Ingress and Service resources and provisions ALB/NLB on AWS.
# Required for exposing the UI microservice through an Application Load Balancer.

resource "helm_release" "aws_load_balancer_controller" {
  name       = "aws-load-balancer-controller"
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  namespace  = "kube-system"
  # Pin to a tested version; update after checking compatibility with EKS 1.34
  version = "1.10.0"

  set {
    name  = "clusterName"
    value = module.eks.cluster_name
  }

  # Service account is created by the chart; the annotation binds it to the IRSA role
  set {
    name  = "serviceAccount.create"
    value = "true"
  }

  set {
    name  = "serviceAccount.name"
    value = "aws-load-balancer-controller"
  }

  set {
    name  = "serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.eks.aws_lbc_role_arn
  }

  set {
    name  = "region"
    value = var.aws_region
  }

  set {
    name  = "vpcId"
    value = module.vpc.output_vpc_id
  }

  depends_on = [module.eks]
}

# ─── Cluster Autoscaler ───────────────────────────────────────────────────────
# Scales node groups up when pods are pending due to insufficient resources
# and scales down when nodes are underutilised.

resource "helm_release" "cluster_autoscaler" {
  name       = "cluster-autoscaler"
  repository = "https://kubernetes.github.io/autoscaler"
  chart      = "cluster-autoscaler"
  namespace  = "kube-system"
  # Pin to a tested version; must match the major.minor of the Kubernetes version
  version = "9.43.0"

  set {
    name  = "autoDiscovery.clusterName"
    value = module.eks.cluster_name
  }

  set {
    name  = "awsRegion"
    value = var.aws_region
  }

  # Service account with IRSA annotation
  set {
    name  = "rbac.serviceAccount.create"
    value = "true"
  }

  set {
    name  = "rbac.serviceAccount.name"
    value = "cluster-autoscaler"
  }

  set {
    name  = "rbac.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
    value = module.eks.cluster_autoscaler_role_arn
  }

  # Prevent the autoscaler from evicting itself
  set {
    name  = "podDisruptionBudget.maxUnavailable"
    value = "1"
  }

  depends_on = [module.eks]
}
