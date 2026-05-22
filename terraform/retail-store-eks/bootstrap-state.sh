#!/usr/bin/env bash
# bootstrap-state.sh
#
# Creates the S3 bucket + DynamoDB table used as Terraform remote state.
# The bucket name is derived from the AWS account ID so it is unique per
# account with no manual configuration required.
#
# Run this ONCE before the first `terraform init` in a new account.
# It is idempotent — safe to re-run if resources already exist.
#
# Usage:
#   chmod +x bootstrap-state.sh
#   ./bootstrap-state.sh                          # us-east-1 (default)
#   AWS_REGION=eu-west-1 ./bootstrap-state.sh     # override region

set -euo pipefail

REGION="${AWS_REGION:-us-east-1}"
DYNAMO_TABLE="retail-store-terraform-locks"

# Derive bucket name from account ID — unique per account, no hard-coding needed
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET="retail-store-tfstate-${ACCOUNT_ID}"

echo "==> AWS account  : $ACCOUNT_ID"
echo "==> Using region : $REGION"
echo "==> S3 bucket    : $BUCKET"
echo "==> DynamoDB     : $DYNAMO_TABLE"
echo ""

# ---- 1. Create the S3 bucket (skip if it already exists) ----
if aws s3api head-bucket --bucket "$BUCKET" --region "$REGION" 2>/dev/null; then
  echo "✓ Bucket already exists — skipping creation"
else
  if [ "$REGION" = "us-east-1" ]; then
    # us-east-1 must NOT pass --create-bucket-configuration (AWS quirk)
    aws s3api create-bucket \
      --bucket "$BUCKET" \
      --region "$REGION"
  else
    aws s3api create-bucket \
      --bucket "$BUCKET" \
      --region "$REGION" \
      --create-bucket-configuration LocationConstraint="$REGION"
  fi
  echo "✓ Bucket created"
fi

# ---- 2. Enable versioning (protects state history) ----
aws s3api put-bucket-versioning \
  --bucket "$BUCKET" \
  --versioning-configuration Status=Enabled
echo "✓ Versioning enabled"

# ---- 3. Enable server-side encryption (AES-256) ----
aws s3api put-bucket-encryption \
  --bucket "$BUCKET" \
  --server-side-encryption-configuration '{
    "Rules": [{
      "ApplyServerSideEncryptionByDefault": {
        "SSEAlgorithm": "AES256"
      },
      "BucketKeyEnabled": true
    }]
  }'
echo "✓ Encryption enabled"

# ---- 4. Block all public access ----
aws s3api put-public-access-block \
  --bucket "$BUCKET" \
  --public-access-block-configuration \
    BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
echo "✓ Public access blocked"

# ---- 5. Create DynamoDB table for state locking (skip if it already exists) ----
TABLE_STATUS=$(aws dynamodb describe-table \
  --table-name "$DYNAMO_TABLE" \
  --region "$REGION" \
  --query "Table.TableStatus" \
  --output text 2>/dev/null || echo "MISSING")

if [ "$TABLE_STATUS" != "MISSING" ]; then
  echo "✓ DynamoDB table already exists — skipping creation"
else
  aws dynamodb create-table \
    --table-name "$DYNAMO_TABLE" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region "$REGION" \
    --tags Key=Project,Value=retail-store-sample-app Key=ManagedBy,Value=terraform
  echo "✓ DynamoDB table created"

  echo "   Waiting for DynamoDB table to become ACTIVE..."
  aws dynamodb wait table-exists --table-name "$DYNAMO_TABLE" --region "$REGION"
  echo "✓ DynamoDB table is ACTIVE"
fi

echo ""
echo "Bootstrap complete."
echo "Bucket : $BUCKET"
echo ""
echo "Run next (from the terraform/retail-store-eks directory):"
echo "  terraform init \\"
echo "    -backend-config=\"bucket=${BUCKET}\" \\"
echo "    -backend-config=\"key=retail-store-eks/terraform.tfstate\" \\"
echo "    -backend-config=\"region=${REGION}\" \\"
echo "    -backend-config=\"encrypt=true\" \\"
echo "    -backend-config=\"dynamodb_table=${DYNAMO_TABLE}\""
