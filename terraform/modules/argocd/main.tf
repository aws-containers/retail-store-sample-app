# Module: argocd
# Deploys ArgoCD into the target cluster using the Helm chart and creates
# repository secrets for GitOps access. The module is intentionally
# limited to cluster-scoped deployment items (namespace, secrets, helm).

resource "kubernetes_namespace" "argocd" {
  count = var.argocd_create_namespace ? 1 : 0

  metadata {
    name = var.argocd_namespace
  }
}

resource "kubernetes_secret" "gitops_repository" {
  metadata {
    name      = "gitops-repo-credentials"
    namespace = var.argocd_namespace
    labels = {
      "argocd.argoproj.io/secret-type" = "repository"
    }
  }

  type = "Opaque"

  data = {
    type      = "git"
    url       = var.gitops_repo_url
    password  = var.gitops_pat
    username  = var.gitops_username
    insecure  = "false"
    enableLFS = "true"
  }

  depends_on = [kubernetes_namespace.argocd]
}

resource "kubernetes_secret" "ecr_registry" {
  count = var.enable_ecr_integration ? 1 : 0

  metadata {
    name      = "ecr-registry-credentials"
    namespace = var.argocd_namespace
    labels = {
      "argocd.argoproj.io/secret-type" = "repository"
    }
  }

  type = "Opaque"

  data = {
    type      = "helm"
    url       = "oci://${var.ecr_repository_prefix}"
    name      = "ecr"
    enableOCI = "true"
  }

  depends_on = [kubernetes_namespace.argocd]
}

resource "helm_release" "argocd" {
  name             = "argocd"
  repository       = "https://argoproj.github.io/argo-helm"
  chart            = "argo-cd"
  namespace        = var.argocd_namespace
  create_namespace = false
  version          = var.argocd_chart_version

  depends_on = [
    kubernetes_namespace.argocd,
    kubernetes_secret.gitops_repository
  ]

  set {
    name  = "global.domain"
    value = "argocd.${var.cluster_name}.local"
  }

  # IRSA annotation for repo server, injected when requested
  dynamic "set" {
    for_each = var.enable_repo_server_irsa_annotation && var.repo_server_iam_role_arn != "" ? [1] : []
    content {
      name  = "repoServer.serviceAccount.annotations.eks\\.amazonaws\\.com/role-arn"
      value = var.repo_server_iam_role_arn
    }
  }

  set {
    name  = "repoServer.serviceAccount.create"
    value = "true"
  }

  set {
    name  = "server.insecure"
    value = "false"
  }

  set {
    name  = "server.ingress.enabled"
    value = "false"
  }

  dynamic "set" {
    for_each = var.enable_ecr_integration ? [1] : []
    content {
      name  = "configs.repositories.ecr.url"
      value = "oci://${var.ecr_repository_prefix}"
    }
  }

  dynamic "set" {
    for_each = var.enable_ecr_integration ? [1] : []
    content {
      name  = "configs.repositories.ecr.type"
      value = "helm"
    }
  }

  dynamic "set" {
    for_each = var.enable_ecr_integration ? [1] : []
    content {
      name  = "configs.repositories.ecr.enableOCI"
      value = "true"
    }
  }

  set {
    name  = "repoServer.resources.requests.cpu"
    value = "100m"
  }

  set {
    name  = "repoServer.resources.requests.memory"
    value = "128Mi"
  }

  set {
    name  = "repoServer.resources.limits.cpu"
    value = "500m"
  }

  set {
    name  = "repoServer.resources.limits.memory"
    value = "512Mi"
  }

  set {
    name  = "server.resources.requests.cpu"
    value = "100m"
  }

  set {
    name  = "server.resources.requests.memory"
    value = "128Mi"
  }

  set {
    name  = "server.resources.limits.cpu"
    value = "500m"
  }

  set {
    name  = "server.resources.limits.memory"
    value = "512Mi"
  }

  dynamic "set" {
    for_each = var.argocd_helm_values_override
    content {
      name  = set.key
      value = set.value
    }
  }
}

# Ensure the SA has the annotation (keeps annotation across helm upgrades)
resource "kubernetes_service_account" "argocd_repo_server" {
  count = var.enable_repo_server_irsa_annotation && var.repo_server_iam_role_arn != "" ? 1 : 0

  metadata {
    name      = "argocd-repo-server"
    namespace = var.argocd_namespace
    annotations = {
      "eks.amazonaws.com/role-arn" = var.repo_server_iam_role_arn
    }
  }

  depends_on = [helm_release.argocd]
}
