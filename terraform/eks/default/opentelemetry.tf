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
      "namespace" = module.retail_app_eks.adot_namespace
    }
    "spec" = {
      "env" = [
        {
          "name"  = "OTEL_SDK_DISABLED"
          "value" = "false"
        },
        {
          "name"  = "OTEL_EXPORTER_OTLP_PROTOCOL"
          "value" = "http/protobuf"
        },
        {
          "name"  = "OTEL_RESOURCE_PROVIDERS_AWS_ENABLED"
          "value" = "true"
        },
        {
          "name"  = "OTEL_METRICS_EXPORTER"
          "value" = "none"
        },
        {
          "name"  = "OTEL_JAVA_GLOBAL_AUTOCONFIGURE_ENABLED"
          "value" = "true"
        }
      ]
      "exporter" = {
        "endpoint" = "http://adot-col-otlp-ingest-collector.${module.retail_app_eks.adot_namespace}:4318"
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
