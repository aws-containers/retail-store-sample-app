# ALB Setup for EKS - retail-store-sample-app

## Problem

The `ui` service was of type `LoadBalancer` but no actual load balancer was being created in AWS.

**Root Causes:**
- AWS Load Balancer Controller was not installed
- Public subnets had no tags — ALB controller couldn't discover them
- No OIDC provider associated with the cluster
- No IngressClass existed

---

## Solution: Complete Terraform Setup (Recommended)

This Terraform configuration automates the entire ALB setup including OIDC provider, IAM policies, service accounts, Helm installation, and Ingress creation.

### Prerequisites

- Terraform >= 1.0
- kubectl configured to access your EKS cluster
- An existing EKS cluster
- Helm provider for Terraform

### Terraform Configuration Files

Create `provider.tf`:

```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_eks_cluster" "main" {
  name = var.cluster_name
}

data "aws_eks_cluster_auth" "main" {
  name = var.cluster_name
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.main.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.main.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.main.token
}

provider "helm" {
  kubernetes {
    host                   = data.aws_eks_cluster.main.endpoint
    cluster_ca_certificate = base64decode(data.aws_eks_cluster.main.certificate_authority[0].data)
    token                  = data.aws_eks_cluster_auth.main.token
  }
}
```

Create `variables.tf`:

```hcl
variable "cluster_name" {
  type        = string
  description = "EKS cluster name"
  default     = "demo-eks"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "us-east-1"
}

variable "vpc_id" {
  type        = string
  description = "VPC ID where EKS cluster is deployed"
}

variable "subnet_ids" {
  type        = list(string)
  description = "List of public subnet IDs for ALB controller to discover"
}

variable "alb_controller_version" {
  type        = string
  description = "Version of AWS Load Balancer Controller"
  default     = "v2.11.0"
}

variable "alb_controller_chart_version" {
  type        = string
  description = "Helm chart version for ALB controller"
  default     = "2.11.0"
}
```

Create `alb-controller.tf`:

```hcl
# Get the OIDC provider thumbprint
data "tls_certificate" "main" {
  url = data.aws_eks_cluster.main.identity[0].oidc[0].issuer
}

# Create OIDC provider
resource "aws_iam_openid_connect_provider" "eks" {
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.main.certificates[0].sha1_fingerprint]
  url             = data.aws_eks_cluster.main.identity[0].oidc[0].issuer
}

# ALB Controller IAM Policy
resource "aws_iam_policy" "alb_controller" {
  name        = "${var.cluster_name}-alb-controller-policy"
  description = "IAM policy for ALB controller"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "elbv2:DescribeLoadBalancers",
          "elbv2:DescribeLoadBalancerAttributes",
          "elbv2:DescribeListeners",
          "elbv2:DescribeListenerCertificates",
          "elbv2:DescribeSSLPolicies",
          "elbv2:DescribeTags",
          "elbv2:DescribeTargetGroups",
          "elbv2:DescribeTargetGroupAttributes",
          "elbv2:DescribeTargetHealth",
          "elbv2:DescribeRules"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "elbv2:CreateLoadBalancer",
          "elbv2:CreateTargetGroup",
          "elbv2:CreateListener",
          "elbv2:DeleteLoadBalancer",
          "elbv2:DeleteTargetGroup",
          "elbv2:DeleteListener",
          "elbv2:ModifyLoadBalancerAttributes",
          "elbv2:ModifyTargetGroupAttributes",
          "elbv2:ModifyTargetGroup",
          "elbv2:ModifyListener",
          "elbv2:RegisterTargets",
          "elbv2:DeregisterTargets",
          "elbv2:CreateListenerRule",
          "elbv2:DeleteListenerRule",
          "elbv2:ModifyRule"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSecurityGroupRules",
          "ec2:AuthorizeSecurityGroupIngress",
          "ec2:RevokeSecurityGroupIngress",
          "ec2:DescribeInstances",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DescribeSubnets",
          "ec2:DescribeVpcs",
          "ec2:DescribeTags",
          "ec2:GetSecurityGroupPolicy"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "wafv2:GetWebACL",
          "wafv2:AssociateWebACL",
          "wafv2:DisassociateWebACL"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogGroups"
        ]
        Resource = "arn:aws:logs:*:*:log-group:/aws/load-balancer-controller/*"
      },
      {
        Effect = "Allow"
        Action = [
          "elasticloadbalancing:*"
        ]
        Resource = "*"
      }
    ]
  })
}

# IAM role for ALB controller service account
resource "aws_iam_role" "alb_controller" {
  name = "${var.cluster_name}-alb-controller-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Effect = "Allow"
        Principal = {
          Federated = aws_iam_openid_connect_provider.eks.arn
        }
        Condition = {
          StringEquals = {
            "${replace(aws_iam_openid_connect_provider.eks.url, "https://", "")}:sub" = "system:serviceaccount:kube-system:aws-load-balancer-controller"
            "${replace(aws_iam_openid_connect_provider.eks.url, "https://", "")}:aud" = "sts.amazonaws.com"
          }
        }
      }
    ]
  })
}

# Attach policy to role
resource "aws_iam_role_policy_attachment" "alb_controller" {
  policy_arn = aws_iam_policy.alb_controller.arn
  role       = aws_iam_role.alb_controller.name
}

# Tag subnets for ALB controller discovery
resource "aws_ec2_tag" "subnet_alb_discovery" {
  for_each    = toset(var.subnet_ids)
  resource_id = each.value
  key         = "kubernetes.io/role/elb"
  value       = "1"
}

resource "aws_ec2_tag" "subnet_cluster" {
  for_each    = toset(var.subnet_ids)
  resource_id = each.value
  key         = "kubernetes.io/cluster/${var.cluster_name}"
  value       = "shared"
}

# Create service account
resource "kubernetes_service_account" "alb_controller" {
  metadata {
    name      = "aws-load-balancer-controller"
    namespace = "kube-system"
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.alb_controller.arn
    }
  }
}

# Install ALB controller using Helm
resource "helm_release" "alb_controller" {
  name       = "aws-load-balancer-controller"
  repository = "https://aws.github.io/eks-charts"
  chart      = "aws-load-balancer-controller"
  namespace  = "kube-system"
  version    = var.alb_controller_chart_version

  set {
    name  = "clusterName"
    value = var.cluster_name
  }

  set {
    name  = "serviceAccount.create"
    value = "false"
  }

  set {
    name  = "serviceAccount.name"
    value = kubernetes_service_account.alb_controller.metadata[0].name
  }

  set {
    name  = "region"
    value = var.aws_region
  }

  set {
    name  = "vpcId"
    value = var.vpc_id
  }

  depends_on = [
    kubernetes_service_account.alb_controller,
    aws_iam_role_policy_attachment.alb_controller
  ]
}

# Wait for ALB controller to be ready
resource "kubernetes_manifest" "alb_controller_wait" {
  manifest = {
    apiVersion = "v1"
    kind       = "Pod"
    metadata = {
      name      = "alb-controller-readiness-check"
      namespace = "kube-system"
    }
  }

  depends_on = [helm_release.alb_controller]

  computed_fields = ["metadata.name"]
}
```

Create `ingress.tf`:

```hcl
# Create IngressClass for ALB
resource "kubernetes_ingress_class" "alb" {
  metadata {
    name = "alb"
  }

  spec {
    controller = "ingress.k8s.aws/alb"
  }

  depends_on = [helm_release.alb_controller]
}

# Create UI Ingress
resource "kubernetes_ingress_v1" "ui" {
  metadata {
    name      = "ui"
    namespace = "default"

    annotations = {
      "alb.ingress.kubernetes.io/scheme"     = "internet-facing"
      "alb.ingress.kubernetes.io/target-type" = "ip"
      "alb.ingress.kubernetes.io/listen-ports" = jsonencode([{ HTTP = 80 }])
    }
  }

  spec {
    ingress_class_name = "alb"

    rule {
      http {
        path {
          path      = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "ui"

              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }

  depends_on = [kubernetes_ingress_class.alb]
}
```

Create `outputs.tf`:

```hcl
output "alb_controller_status" {
  value       = "Installed and configured"
  description = "Status of AWS Load Balancer Controller"
}

output "ui_ingress_name" {
  value       = kubernetes_ingress_v1.ui.metadata[0].name
  description = "Name of UI Ingress"
}

output "alb_controller_role_arn" {
  value       = aws_iam_role.alb_controller.arn
  description = "ARN of IAM role for ALB controller"
}

output "oidc_provider_arn" {
  value       = aws_iam_openid_connect_provider.eks.arn
  description = "ARN of OIDC provider"
}

output "get_alb_dns_command" {
  value       = "kubectl get ingress ui -n default -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
  description = "Command to get ALB DNS name"
}
```

### Deploy with Terraform

1. Initialize Terraform:

    ```bash
    terraform init
    ```

2. Create `terraform.tfvars`:

    ```hcl
    cluster_name = "demo-eks"
    aws_region   = "us-east-1"
    vpc_id       = "<YOUR_VPC_ID>"
    subnet_ids   = ["<SUBNET_1>", "<SUBNET_2>", "<SUBNET_3>"]
    ```

3. Plan the configuration:

    ```bash
    terraform plan
    ```

4. Apply the configuration:

    ```bash
    terraform apply
    ```

### Verify the Setup

1. Check ALB controller deployment:

    ```bash
    kubectl rollout status deployment aws-load-balancer-controller -n kube-system --timeout=90s
    ```

2. Verify IngressClass is created:

    ```bash
    kubectl get ingressclass
    ```

3. Check UI Ingress:

    ```bash
    kubectl get ingress ui
    ```

4. Get ALB DNS name:

    ```bash
    kubectl get ingress ui -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
    ```

---

## Manual Setup (Legacy - Not Recommended)

If you prefer to manually set up ALB without Terraform:

### Prerequisites

> AWS Account ID, VPC ID, and Subnet IDs **change across environments and accounts**.
> Always fetch these values fresh from AWS before running any commands below.

```bash
# Get AWS Account ID
aws sts get-caller-identity --query "Account" --output text

# Get default VPC ID
aws ec2 describe-vpcs \
  --filters "Name=isDefault,Values=true" \
  --query "Vpcs[0].VpcId" --output text

# Get default subnet IDs
aws ec2 describe-subnets \
  --filters "Name=defaultForAz,Values=true" \
  --query "Subnets[*].SubnetId" --output text
```

### Manual Commands

#### 1. Tag Public Subnets

```bash
aws ec2 create-tags \
  --region us-east-1 \
  --resources <SUBNET_IDS> \
  --tags Key=kubernetes.io/cluster/<CLUSTER_NAME>,Value=shared Key=kubernetes.io/role/elb,Value=1
```

#### 2. Associate OIDC Provider

```bash
eksctl utils associate-iam-oidc-provider \
  --cluster <CLUSTER_NAME> \
  --region us-east-1 \
  --approve
```

#### 3. Create IAM Policy

```bash
curl -sO https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.11.0/docs/install/iam_policy.json

aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json \
  --region us-east-1
```

#### 4. Create IRSA Service Account

```bash
eksctl create iamserviceaccount \
  --cluster=<CLUSTER_NAME> \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::<ACCOUNT_ID>:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --region us-east-1 \
  --approve
```

#### 5. Install Helm & ALB Controller

```bash
curl -sL https://get.helm.sh/helm-v3.20.2-darwin-arm64.tar.gz -o /tmp/helm.tar.gz
tar -xzf /tmp/helm.tar.gz -C /tmp
cp /tmp/darwin-arm64/helm ~/helm

~/helm repo add eks https://aws.github.io/eks-charts
~/helm repo update

~/helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=<CLUSTER_NAME> \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=us-east-1 \
  --set vpcId=<VPC_ID>
```

#### 6. Create UI Ingress

```bash
kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ui
  namespace: default
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP": 80}]'
spec:
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: ui
                port:
                  number: 80
EOF
```

---

## Cluster Info

| Property | Value |
|---|---|
| Cluster | `demo-eks` |
| Region | `us-east-1` |
| ALB Controller Version | v2.11.0 |
