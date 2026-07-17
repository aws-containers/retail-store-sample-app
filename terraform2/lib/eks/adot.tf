resource "kubernetes_namespace_v1" "adot" {
  metadata {
    name = "opentelemetry-operator-system"

    labels = {
      # Prerequisite for EKS addon
      "control-plane" = "controller-manager"
    }
  }
}

resource "aws_eks_addon" "adot" {
  cluster_name                = module.eks_cluster.cluster_name
  addon_name                  = "adot"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
  preserve                    = false

  configuration_values = var.opentelemetry_enabled ? local.collector_configuration : "{\"collector\": {}}"

  tags = {
    RoleVersion        = try(kubernetes_role_v1.adot.metadata[0].resource_version, ""),
    ClusterRoleVersion = try(kubernetes_cluster_role_v1.adot.metadata[0].resource_version, "")
  }

  depends_on = [module.eks_blueprints_addons]
}

resource "kubernetes_role_v1" "adot" {
  metadata {
    name      = "eks:addon-manager"
    namespace = kubernetes_namespace_v1.adot.metadata[0].name
  }

  rule {
    api_groups     = [""]
    resources      = ["serviceaccounts"]
    resource_names = ["opentelemetry-operator-controller-manager"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["rbac.authorization.k8s.io"]
    resources      = ["roles"]
    resource_names = ["opentelemetry-operator-leader-election-role"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["rbac.authorization.k8s.io"]
    resources      = ["rolebindings"]
    resource_names = ["opentelemetry-operator-leader-election-rolebinding"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = [""]
    resources      = ["services"]
    resource_names = ["opentelemetry-operator-controller-manager-metrics-service", "opentelemetry-operator-webhook-service"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["apps"]
    resources      = ["deployments"]
    resource_names = ["opentelemetry-operator-controller-manager"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["cert-manager.io"]
    resources      = ["certificates", "issuers"]
    resource_names = ["opentelemetry-operator-serving-cert", "opentelemetry-operator-selfsigned-issuer"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = [""]
    resources  = ["configmaps"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = [""]
    resources  = ["configmaps/status"]
    verbs      = ["get", "update", "patch"]
  }
  rule {
    api_groups = [""]
    resources  = ["events"]
    verbs      = ["create", "patch"]
  }
  rule {
    api_groups = [""]
    resources  = ["pods"]
    verbs      = ["list"]
  }
}

resource "kubernetes_role_binding_v1" "adot" {
  metadata {
    name      = "eks:addon-manager"
    namespace = kubernetes_namespace_v1.adot.metadata[0].name
  }

  subject {
    kind      = "User"
    name      = "eks:addon-manager"
    api_group = "rbac.authorization.k8s.io"
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "Role"
    name      = "eks:addon-manager"
  }
}

resource "kubernetes_cluster_role_v1" "adot" {
  metadata {
    name = "eks:addon-manager-otel"
  }

  rule {
    api_groups     = ["apiextensions.k8s.io"]
    resources      = ["customresourcedefinitions"]
    resource_names = ["opentelemetrycollectors.opentelemetry.io", "instrumentations.opentelemetry.io"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = [""]
    resources      = ["namespaces"]
    resource_names = [kubernetes_namespace_v1.adot.metadata[0].name]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["rbac.authorization.k8s.io"]
    resources      = ["clusterroles"]
    resource_names = ["opentelemetry-operator-manager-role", "opentelemetry-operator-metrics-reader", "opentelemetry-operator-proxy-role"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["rbac.authorization.k8s.io"]
    resources      = ["clusterrolebindings"]
    resource_names = ["opentelemetry-operator-manager-rolebinding", "opentelemetry-operator-proxy-rolebinding"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups     = ["admissionregistration.k8s.io"]
    resources      = ["mutatingwebhookconfigurations", "validatingwebhookconfigurations"]
    resource_names = ["opentelemetry-operator-mutating-webhook-configuration", "opentelemetry-operator-validating-webhook-configuration"]
    verbs          = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["networking.k8s.io"]
    resources  = ["ingresses"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    non_resource_urls = ["/metrics"]
    verbs             = ["get"]
  }
  rule {
    api_groups = [""]
    resources  = ["configmaps"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = [""]
    resources  = ["events"]
    verbs      = ["create", "patch"]
  }
  rule {
    api_groups = [""]
    resources  = ["namespaces"]
    verbs      = ["list", "watch"]
  }
  rule {
    api_groups = [""]
    resources  = ["serviceaccounts"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = [""]
    resources  = ["services"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["apps"]
    resources  = ["daemonsets"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["apps"]
    resources  = ["deployments"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["apps"]
    resources  = ["replicasets"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["apps"]
    resources  = ["statefulsets"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["autoscaling"]
    resources  = ["horizontalpodautoscalers"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["coordination.k8s.io"]
    resources  = ["leases"]
    verbs      = ["create", "get", "list", "update"]
  }
  rule {
    api_groups = ["opentelemetry.io"]
    resources  = ["opentelemetrycollectors"]
    verbs      = ["create", "delete", "get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["opentelemetry.io"]
    resources  = ["opentelemetrycollectors/finalizers"]
    verbs      = ["get", "patch", "update"]
  }
  rule {
    api_groups = ["opentelemetry.io"]
    resources  = ["opentelemetrycollectors/status"]
    verbs      = ["get", "patch", "update"]
  }
  rule {
    api_groups = ["opentelemetry.io"]
    resources  = ["instrumentations"]
    verbs      = ["get", "list", "patch", "update", "watch"]
  }
  rule {
    api_groups = ["authentication.k8s.io"]
    resources  = ["tokenreviews"]
    verbs      = ["create"]
  }
  rule {
    api_groups = ["authorization.k8s.io"]
    resources  = ["subjectaccessreviews"]
    verbs      = ["create"]
  }
}

resource "kubernetes_cluster_role_binding_v1" "adot" {
  metadata {
    name = "eks:addon-manager-otel"
  }
  subject {
    kind      = "User"
    name      = "eks:addon-manager"
    api_group = "rbac.authorization.k8s.io"
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "eks:addon-manager-otel"
  }
}

module "iam_assumable_role_adot_amp" {
  source       = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version      = "~> 5.58.0"
  create_role  = true
  role_name    = "${var.environment_name}-adot-col-xray"
  provider_url = module.eks_cluster.cluster_oidc_issuer_url
  role_policy_arns = [
    "arn:${data.aws_partition.current.partition}:iam::aws:policy/AWSXRayDaemonWriteAccess"
  ]
  oidc_fully_qualified_subjects = ["system:serviceaccount:${kubernetes_namespace_v1.adot.metadata[0].name}:adot-col-otlp-ingest"]
}

module "iam_assumable_role_adot_logs" {
  source       = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version      = "~> 5.58.0"
  create_role  = true
  role_name    = "${var.environment_name}-adot-col-logs"
  provider_url = module.eks_cluster.cluster_oidc_issuer_url
  role_policy_arns = [
    "arn:${data.aws_partition.current.partition}:iam::aws:policy/CloudWatchAgentServerPolicy"
  ]
  oidc_fully_qualified_subjects = ["system:serviceaccount:${kubernetes_namespace_v1.adot.metadata[0].name}:adot-col-container-logs"]
}

locals {
  collector_configuration = <<EOF
{
  "collector": {
    "otlpIngest": {
      "serviceAccount": {
        "annotations": {
          "eks.amazonaws.com/role-arn": "${module.iam_assumable_role_adot_amp.iam_role_arn}"
        }
      },
      "pipelines": {
        "traces": {
          "xray": {
            "enabled": true
          }
        }
      }
    }
  }
}
EOF
}
