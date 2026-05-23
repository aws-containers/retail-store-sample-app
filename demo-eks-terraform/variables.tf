####################################################################
#
# Variables used. All have defaults
#
####################################################################

# KK Playground. Cluster must be called 'demo-eks'
variable "cluster_name" {
  type        = string
  description = "Name of the cluster"
  default     = "demo-eks"
}

# KK Playground. Cluster role must be called 'eksClusterRole'
variable "cluster_role_name" {
  type        = string
  description = "Name of the cluster role"
  default     = "eksClusterRole"
}

# In KK playground and for some EKS labs, the role is not predefined.
# In some other EKS labs, the service role exists already.
# This variable is initialized as an environment variable source
# by check-environment.sh if it is required to be "true"
variable "use_predefined_role" {
  type        = bool
  description = "Whether to use predefined cluster service role, or create one."
  default     = false
}

# KK Playground. Node role must be called 'eksWorkerNodeRole'
variable "node_role_name" {
  type        = string
  description = "Name of node role"
  default     = "eksWorkerNodeRole"
}

# KK Playground. Policy role must be called 'eksPolicy'
variable "additional_policy_name" {
    type = string
    description = "Name of IAM::Policy created for additional permissions"
    default = "eksPolicy"
}

# KodeKloud sandbox caps the account at 10 vCPU / 20 GiB total. With the
# default t3.medium worker (2 vCPU, 4 GiB), max=2 keeps cluster usage at
# 4 vCPU / 8 GiB and leaves headroom for any KodeKloud-managed instances.
# Do not raise max above 2 without also revisiting the instance type.
variable "node_group_desired_capacity" {
  type        = number
  description = "Desired capacity of Node Group ASG."
  default     = 2
}
variable "node_group_max_size" {
  type        = number
  description = "Maximum size of Node Group ASG. Capped at 2 to stay under the 10 vCPU sandbox limit."
  default     = 2
}

variable "node_group_min_size" {
  type        = number
  description = "Minimum size of Node Group ASG."
  default     = 1
}

