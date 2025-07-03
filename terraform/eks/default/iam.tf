module "iam_assumable_role_carts" {
  source                        = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version                       = "~> 5.58.0"
  create_role                   = true
  role_name                     = "${var.environment_name}-carts-dynamo"
  provider_url                  = module.retail_app_eks.eks_oidc_issuer_url
  role_policy_arns              = [module.dependencies.carts_dynamodb_policy_arn]
  oidc_fully_qualified_subjects = ["system:serviceaccount:carts:carts"]

  tags = module.tags.result
}