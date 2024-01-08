resource "kubectl_manifest" "otel_instrumentation" {
  count = var.opentelemetry_enabled ? 1 : 0

  depends_on = [
    time_sleep.workloads
  ]

  yaml_body = yamlencode({
    "apiVersion" = "opentelemetry.io/v1alpha1"
    "kind"       = "Instrumentation"
    "metadata" = {
      "name"      = "default-instrumentation"
      "namespace" = "${module.retail_app_eks.adot_namespace}"
    }
    "spec" = {
      "env" = [
        {
          "name"  = "OTEL_JAVAAGENT_ENABLED"
          "value" = "true"
        },
      ]
      "exporter" = {
        "endpoint" = "http://adot-col-otlp-ingest-collector.${module.retail_app_eks.adot_namespace}:4317"
      }
      "propagators" = [
        "tracecontext",
        "baggage",
      ]
      "sampler" = {
        "type" = "always_on"
      }
    }
  })
}

locals {
  opentelemetry_instrumentation = var.opentelemetry_enabled ? "${module.retail_app_eks.adot_namespace}/${yamldecode(kubectl_manifest.otel_instrumentation[0].yaml_body_parsed).metadata.name}" : ""
}
