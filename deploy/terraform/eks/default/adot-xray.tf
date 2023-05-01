module "iam_assumable_role_adot_xray" {
  source                        = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version                       = "~> v5.5.0"
  create_role                   = true
  role_name                     = "${var.environment_name}-adot-xray"
  provider_url                  = module.retail_app_eks.eks_oidc_issuer_url
  role_policy_arns              = ["arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"]
  oidc_fully_qualified_subjects = ["system:serviceaccount:opentelemetry:adot-xray-opentelemetry-collector"]

  tags = module.tags.result
}