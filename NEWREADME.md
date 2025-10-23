# Retail Store App Deployment & Architecture Guide

## Overview
This deploys a microservices retail app (UI, Catalog, Cart, Orders, Checkout) to a new EKS cluster using Terraform IaC.

## Architecture
- VPC: 2 public/2 private subnets.
- EKS: Managed nodes, IRSA for IAM.
- Networking: AWS LB Controller
- CI/CD: GitHub Actions on main (apply), PRs (plan)

# 🧩 Deployment Summary

The **Retail Store Sample Application** was successfully deployed to an **Amazon EKS (Elastic Kubernetes Service)** cluster in the **us-west-2** region. The deployment process involved creating the EKS cluster using **Terraform** and applying the Kubernetes manifests to deploy all application components.

After deployment, the UI service was exposed using a **LoadBalancer** service type, and AWS provisioned an internet-facing **Network Load Balancer (NLB)** with the following details:

- **Service Name:** ui
- **Type:** LoadBalancer
- **Cluster IP:** 172.20.117.129
- **External DNS:** k8s-default-ui-47eebf9f2a-48052e1126820cc3.elb.us-west-2.amazonaws.com
- **Port:** 80 (mapped to container port 8080)

The deployment was verified with the following checks:

- `kubectl get svc ui` confirmed the LoadBalancer and external DNS name were created.
- `curl -I http://<ELB-DNS>` returned `HTTP/1.1 200 OK`, confirming that the application responded successfully through the load balancer.
- Target instances were verified as healthy in the NLB target group via `aws elbv2 describe-target-health`.

> **Note:** Although the application responded correctly to HTTP requests, the UI did not render in a browser, likely due to network security group or subnet routing configurations. However, the core deployment and service exposure were successfully implemented and validated via command-line tests.

---

## Step 6: IAM User Configuration Summary

To enable secure and limited access for the development team, an **IAM user** named `dev-team-user` was created with **programmatic access only**. The user was granted **read-only permissions** to the EKS cluster (`retail-store`) and its associated CloudWatch logs.  

Two policies were attached:

1. **AmazonEKSClusterPolicy** — allows viewing and describing EKS clusters.
2. **Custom Policy (DevTeamViewLogsDescribePolicy)** — provides access to CloudWatch log groups and events under `/aws/eks/retail-store/*`, and allows describing and listing clusters without administrative privileges.

Access keys were generated and securely stored to allow the dev team to connect via CLI for monitoring and inspection purposes. This configuration ensures the team can view cluster states and logs without making any modifications, maintaining the **principle of least privilege**.

---

# Instructions for Dev User

To access and monitor the **EKS cluster**, the `dev-team-user` must install the **AWS CLI** and **kubectl** tools on their local system.  

After installation, configure the AWS CLI using the access keys provided in the google drive folder provided by running:

`bash`
aws configure

Enter the Access Key ID and Secret Access Key when prompted.

Next, connect to the cluster by updating the kubeconfig file with the following command:

aws eks update-kubeconfig --name retail-store --region us-west-2

Once configured, the user can interact with the EKS cluster using read-only commands such as:

kubectl get pods           # List running pods
kubectl describe pod <pod-name>  # View detailed pod information
kubectl logs <pod-name>    # Check application logs
kubectl get svc            # View deployed services

This configuration allows the dev team user to inspect resources, view logs, and monitor application health without administrative privileges.
