module "iam_assumable_role_adot_xray" {
  source                        = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version                       = "~> v5.5.0"
  create_role                   = true
  role_name                     = "${var.environment_name}-opentelemetry-collector"
  provider_url                  = module.retail_app_eks.eks_oidc_issuer_url
  role_policy_arns              = ["arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"]
  oidc_fully_qualified_subjects = ["system:serviceaccount:opentelemetry:opentelemetry-collector"]

  tags = module.tags.result
}

resource "kubernetes_namespace_v1" "opentelemetry" {
  count = var.opentelemetry_enabled ? 1 : 0

  depends_on = [
    null_resource.addons_blocker
  ]
  
  metadata {
    name = "opentelemetry"
  }
}

resource "helm_release" "opentelemetry" {
  count = var.opentelemetry_enabled ? 1 : 0
  
  name       = "opentelemetry"
  chart      = "../../../kubernetes/charts/opentelemetry"

  namespace  = kubernetes_namespace_v1.opentelemetry[0].metadata[0].name

  values = [
    templatefile("${path.module}/values/opentelemetry.yaml", { 
      adot_xray_role_arn = module.iam_assumable_role_adot_xray.iam_role_arn
    })
  ]
}