# Deployment Guide - Project Bedrock

## Prerequisites
- AWS CLI installed and configured.
- Terraform installed (>= 1.3.0).
- `kubectl` installed.

## Step 1: Initialize Remote State
Run the setup script to create the S3 bucket and DynamoDB table for Terraform state.
```bash
cd terraform
./setup_backend.sh
```
*Note: Ensure your `terraform/backend.tf` matches the bucket name created by the script (defaults to `terraform-state-bedrock-alt-soe-025-0959`).*

## Step 2: Deploy Infrastructure
1. Initialize Terraform:
   ```bash
   terraform init
   ```
2. Validate configuration:
   ```bash
   terraform validate
   ```
3. Plan and Apply:
   ```bash
   terraform apply
   ```
   *Type `yes` when prompted.*

## Step 3: Verify Deployment
1. **Update Kubeconfig**:
   ```bash
   aws eks update-kubeconfig --region us-east-1 --name project-bedrock-cluster
   ```
2. **Check Nodes**:
   ```bash
   kubectl get nodes
   ```
3. **Check Application**:
   ```bash
   kubectl get pods -n retail-app
   ```
4. **Get Access Keys for Grading**:
   The `terraform apply` command will output `bedrock_dev_view_access_key` and `bedrock_dev_view_secret_key`. Save these securely.

## Step 4: Verify Serverless (Extension)
1. Upload a file to the S3 bucket:
   ```bash
   aws s3 cp test-image.jpg s3://bedrock-assets-alt-soe-025-0959/
   ```
2. Check CloudWatch Logs for the Lambda function `bedrock-asset-processor`.

## Step 5: Generage Grading Data
Run this command in the `terraform` directory:
```bash
terraform output -json > grading.json
```
Commit this file to your repository.

## Step 6: Access the Application
Get the Load Balancer URL:
```bash
kubectl get ingress -n retail-app retail-store-ingress
```
Access the ADDRESS in your browser.
