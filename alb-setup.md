# ALB Setup for EKS - retail-store-sample-app

## Problem

The `ui` service was of type `LoadBalancer` but no actual load balancer was being created in AWS.

**Root Causes:**
- AWS Load Balancer Controller was not installed
- Public subnets had no tags — ALB controller couldn't discover them
- No OIDC provider associated with the cluster
- No IngressClass existed

---

## Fix: Step-by-Step Commands

### 1. Tag Public Subnets

ALB controller requires specific tags on subnets to discover them.

```bash
aws ec2 create-tags \
  --region us-east-1 \
  --resources subnet-09f65d9104c73a580 subnet-091d2844ee07b92c0 subnet-0413ef586e3d02344 \
  --tags Key=kubernetes.io/cluster/demo-eks,Value=shared Key=kubernetes.io/role/elb,Value=1
```

### 2. Associate OIDC Provider with the Cluster

Required for IAM Roles for Service Accounts (IRSA).

```bash
eksctl utils associate-iam-oidc-provider \
  --cluster demo-eks \
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

```bash
eksctl create iamserviceaccount \
  --cluster=demo-eks \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::590183819111:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --region us-east-1 \
  --approve
```

### 5. Install Helm

```bash
curl -sL https://get.helm.sh/helm-v3.20.2-darwin-arm64.tar.gz -o /tmp/helm.tar.gz
tar -xzf /tmp/helm.tar.gz -C /tmp
cp /tmp/darwin-arm64/helm ~/helm
```

### 6. Install AWS Load Balancer Controller via Helm

```bash
~/helm repo add eks https://aws.github.io/eks-charts
~/helm repo update

~/helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=demo-eks \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=us-east-1 \
  --set vpcId=vpc-04b0cba910448c78a
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

| Property | Value |
|---|---|
| Cluster | demo-eks |
| Region | us-east-1 |
| VPC | vpc-04b0cba910448c78a |
| Subnets | subnet-09f65d9104c73a580, subnet-091d2844ee07b92c0, subnet-0413ef586e3d02344 |
| ALB Controller Version | v2.11.0 |
