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
      aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.demo_eks.name}
      kubectl apply -f ${path.module}/retail-store.yaml
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

output "ui_alb_dns_command" {
  value       = "kubectl get ingress ui -n default -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
  description = "Run this after apply to get the ALB DNS for the UI"
}
