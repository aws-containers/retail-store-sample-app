variable "role_name" {
  description = "Name of the IAM role created for service account federation."
  type        = string
}

variable "oidc_provider_arn" {
  description = "ARN of the EKS OIDC provider used in the trust policy."
  type        = string
}

variable "namespace_service_accounts" {
  description = "List of namespace:serviceaccount values allowed to assume the role."
  type        = list(string)
}

variable "attach_load_balancer_controller_policy" {
  description = "Attach the AWS managed policy set required by AWS Load Balancer Controller."
  type        = bool
  default     = false
}

variable "attach_ebs_csi_policy" {
  description = "Attach the AWS managed policy set required by the EBS CSI driver."
  type        = bool
  default     = false
}

variable "attach_cluster_autoscaler_policy" {
  description = "Attach the policy required by Cluster Autoscaler."
  type        = bool
  default     = false
}

variable "cluster_autoscaler_cluster_names" {
  description = "Cluster names allowed by the Cluster Autoscaler policy conditions."
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags applied to IAM role resources."
  type        = map(string)
  default     = {}
}
