# ALB Setup for EKS - retail-store-sample-app

## Problem

The `ui` service was of type `LoadBalancer` but no actual load balancer was being created in AWS.

**Root Causes:**
- AWS Load Balancer Controller was not installed
- Public subnets had no tags — ALB controller couldn't discover them
- No OIDC provider associated with the cluster
- No IngressClass existed

---

> **⚠️ IMPORTANT — Read Before Starting**
>
> AWS Account ID, VPC ID, and Subnet IDs **change across environments and accounts**.
> Always fetch these values fresh from AWS before running any commands below.
> Do **not** hardcode or reuse values from a previous run.
>
> Run the following commands first and note down the outputs:
>
> ```bash
> # Get AWS Account ID
> aws sts get-caller-identity --query "Account" --output text
>
> # Get default VPC ID
> aws ec2 describe-vpcs \
>   --filters "Name=isDefault,Values=true" \
>   --query "Vpcs[0].VpcId" --output text
>
> # Get default subnet IDs
> aws ec2 describe-subnets \
>   --filters "Name=defaultForAz,Values=true" \
>   --query "Subnets[*].SubnetId" --output text
> ```
>
> Replace all placeholders (`<ACCOUNT_ID>`, `<VPC_ID>`, `<SUBNET_IDS>`) in the commands below with the values from above before executing.

---

## Fix: Step-by-Step Commands

### 1. Tag Public Subnets

ALB controller requires specific tags on subnets to discover them.

> Replace `<SUBNET_IDS>` with space-separated subnet IDs from the pre-check above.
> Replace `<CLUSTER_NAME>` with your EKS cluster name.

```bash
aws ec2 create-tags \
  --region us-east-1 \
  --resources <SUBNET_IDS> \
  --tags Key=kubernetes.io/cluster/<CLUSTER_NAME>,Value=shared Key=kubernetes.io/role/elb,Value=1
```

### 2. Associate OIDC Provider with the Cluster

Required for IAM Roles for Service Accounts (IRSA).

```bash
eksctl utils associate-iam-oidc-provider \
  --cluster <CLUSTER_NAME> \
  --region us-east-1 \
  --approve
```

### 3. Create IAM Policy for ALB Controller

```bash
curl -sO https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.11.0/docs/install/iam_policy.json

aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json \
  --region us-east-1
```

### 4. Create IRSA Service Account

> Replace `<ACCOUNT_ID>` with your AWS account ID from the pre-check above.

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

### 5. Install Helm

> Adjust the download URL for your OS/architecture if not on macOS ARM64.
> Check latest release at: https://github.com/helm/helm/releases

```bash
curl -sL https://get.helm.sh/helm-v3.20.2-darwin-arm64.tar.gz -o /tmp/helm.tar.gz
tar -xzf /tmp/helm.tar.gz -C /tmp
cp /tmp/darwin-arm64/helm ~/helm
```

### 6. Install AWS Load Balancer Controller via Helm

> Replace `<VPC_ID>` with your VPC ID from the pre-check above.

```bash
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

### 7. Verify Controller is Running

```bash
kubectl rollout status deployment aws-load-balancer-controller -n kube-system --timeout=90s
```

### 8. Create ALB Ingress for UI Service

Create `ui-ingress.yaml`:

```yaml
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
```

Apply it:

```bash
kubectl apply -f ui-ingress.yaml
```

### 9. Get ALB DNS

```bash
kubectl get ingress ui
```

---

## Cluster Info

> Fill this table after running the pre-check commands at the top. Keep it updated per environment.

| Property | Value |
|---|---|
| Cluster | `<CLUSTER_NAME>` |
| Region | `<REGION>` |
| Account ID | `<ACCOUNT_ID>` |
| VPC | `<VPC_ID>` |
| Subnets | `<SUBNET_IDS>` |
| ALB Controller Version | v2.11.0 |
