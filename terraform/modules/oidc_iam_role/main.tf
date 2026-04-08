module "this" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.0"

  role_name = var.role_name

  attach_load_balancer_controller_policy = var.attach_load_balancer_controller_policy
  attach_ebs_csi_policy                  = var.attach_ebs_csi_policy
  attach_cluster_autoscaler_policy       = var.attach_cluster_autoscaler_policy
  cluster_autoscaler_cluster_names       = var.cluster_autoscaler_cluster_names

  oidc_providers = {
    main = {
      provider_arn               = var.oidc_provider_arn
      namespace_service_accounts = var.namespace_service_accounts
    }
  }

  tags = var.tags
}
