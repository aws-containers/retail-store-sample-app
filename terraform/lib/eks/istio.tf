resource "kubernetes_namespace_v1" "istio" {
  count = var.istio_enabled ? 1 : 0

  metadata {
    name = "istio-system"
  }
}

resource "helm_release" "istio_base" {
  count = var.istio_enabled ? 1 : 0

  name       = "istio-base"
  repository = "https://istio-release.storage.googleapis.com/charts"
  chart      = "base"
  namespace  = kubernetes_namespace_v1.istio[0].metadata[0].name
  wait       = true

  set {
    name  = "defaultRevision"
    value = "default"
  }
}

resource "helm_release" "istiod" {
  count = var.istio_enabled ? 1 : 0

  depends_on = [
    helm_release.istio_base
  ]

  name       = "istiod"
  repository = "https://istio-release.storage.googleapis.com/charts"
  chart      = "istiod"
  namespace  = kubernetes_namespace_v1.istio[0].metadata[0].name
  wait       = true

  set {
    name  = "defaultRevision"
    value = "default"
  }
}

resource "kubernetes_namespace_v1" "istio_ingress" {
  count = var.istio_enabled ? 1 : 0

  metadata {
    name = "istio-ingress"
  }
}

resource "helm_release" "istio_ingress" {
  count = var.istio_enabled ? 1 : 0

  depends_on = [
    helm_release.istiod,
    null_resource.addons_blocker
  ]

  name       = "istio-ingress"
  repository = "https://istio-release.storage.googleapis.com/charts"
  chart      = "gateway"
  namespace  = kubernetes_namespace_v1.istio_ingress[0].metadata[0].name
  wait       = true

  values = [
    yamlencode(
      {
        labels = {
          istio = "ingressgateway"
        }
        service = {
          annotations = {
            "service.beta.kubernetes.io/aws-load-balancer-type"            = "external"
            "service.beta.kubernetes.io/aws-load-balancer-nlb-target-type" = "ip"
            "service.beta.kubernetes.io/aws-load-balancer-scheme"          = "internet-facing"
            "service.beta.kubernetes.io/aws-load-balancer-attributes"      = "load_balancing.cross_zone.enabled=true"
          }
        }
      }
    )
  ]
}
