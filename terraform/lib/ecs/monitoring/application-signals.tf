# Enable Application Signals discovery for the account
resource "awscc_applicationsignals_discovery" "this" {}

# --- SLOs (FR-3.2) ---

locals {
  slo_definitions = {
    ui-availability       = { service = "ui", metric_type = "AVAILABILITY", threshold = 99.9 }
    ui-latency            = { service = "ui", metric_type = "LATENCY", threshold = 500 }
    catalog-availability  = { service = "catalog", metric_type = "AVAILABILITY", threshold = 99.9 }
    catalog-latency       = { service = "catalog", metric_type = "LATENCY", threshold = 200 }
    orders-availability   = { service = "orders", metric_type = "AVAILABILITY", threshold = 99.95 }
    orders-latency        = { service = "orders", metric_type = "LATENCY", threshold = 300 }
    carts-availability    = { service = "carts", metric_type = "AVAILABILITY", threshold = 99.9 }
    carts-latency         = { service = "carts", metric_type = "LATENCY", threshold = 100 }
    checkout-availability = { service = "checkout", metric_type = "AVAILABILITY", threshold = 99.95 }
    checkout-latency      = { service = "checkout", metric_type = "LATENCY", threshold = 500 }
  }
}

resource "awscc_applicationsignals_service_level_objective" "this" {
  for_each = var.application_signals_slos_enabled ? local.slo_definitions : {}

  name        = "${var.environment_name}-${each.key}"
  description = "SLO for ${each.value.service} ${lower(each.value.metric_type)}"

  sli = {
    comparison_operator = each.value.metric_type == "AVAILABILITY" ? "GreaterThanOrEqualTo" : "LessThan"
    metric_threshold    = each.value.threshold

    sli_metric = {
      key_attributes = {
        "Type"        = "Service"
        "Name"        = each.value.service
        "Environment" = var.cluster_name
      }
      metric_type    = each.value.metric_type
      period_seconds = 300
      statistic      = each.value.metric_type == "LATENCY" ? "p99" : null
    }
  }

  goal = {
    attainment_goal = each.value.metric_type == "AVAILABILITY" ? each.value.threshold : 99.9
    interval = {
      rolling_interval = {
        duration      = 30
        duration_unit = "DAY"
      }
    }
  }

  burn_rate_configurations = [
    { look_back_window_minutes = 60 },
    { look_back_window_minutes = 360 },
    { look_back_window_minutes = 10080 }
  ]

  tags = [for k, v in var.tags : { key = k, value = v }]

  depends_on = [awscc_applicationsignals_discovery.this]
}
