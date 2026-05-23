####################################################################
#
# Retail-store sample app + ALB ingress.
#
# Applied with `kubectl apply -f` after the cluster, nodes, and
# ALB controller are ready. Using a single null_resource here is
# simpler than splitting the 30+ resources in retail-store.yaml
# into individual kubernetes_manifest blocks.
#
####################################################################

resource "null_resource" "retail_store" {
  triggers = {
    manifest_sha = filesha256("${path.module}/retail-store.yaml")
    cluster_name = aws_eks_cluster.demo_eks.name
  }

  provisioner "local-exec" {
    command = <<-EOT
      set -e
      aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.demo_eks.name}
      kubectl apply -f ${path.module}/retail-store.yaml

      # Block until the ALB controller publishes the ingress hostname so the
      # kubernetes_ingress_v1 data source below sees a stable value when read.
      for i in $(seq 1 60); do
        host=$(kubectl get ingress ui -n default -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || true)
        if [ -n "$host" ]; then
          echo "ALB hostname: $host"
          exit 0
        fi
        echo "Waiting for ALB hostname ($i/60)..."
        sleep 10
      done
      echo "WARNING: ALB hostname not available after 10 minutes"
    EOT
  }

  provisioner "local-exec" {
    when    = destroy
    command = "kubectl delete -f ${path.module}/retail-store.yaml --ignore-not-found=true || true"
  }

  depends_on = [
    helm_release.alb_controller,
    kubernetes_config_map.aws_auth,
    aws_cloudformation_stack.autoscaling_group,
  ]
}

data "kubernetes_ingress_v1" "ui" {
  metadata {
    name      = "ui"
    namespace = "default"
  }
  depends_on = [null_resource.retail_store]
}

output "ui_alb_url" {
  value       = "http://${data.kubernetes_ingress_v1.ui.status[0].load_balancer[0].ingress[0].hostname}"
  description = "Public URL for the retail-store UI (ALB)"
}
