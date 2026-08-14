# Terraform Foundation

This `terraform/` directory contains the Day 12 Terraform foundation for the AWS Retail Store DevOps Platform. The goal of this stage is to define the structure, validation path, environment layout, module boundaries, tagging rules, and safe state handling before creating larger AWS platform resources.

## Current outcome

The active root module is:

- `terraform/env/dev`

The current reusable modules are:

- `terraform/modules/vpc`
- `terraform/modules/ecr`
- `terraform/modules/iam/github-oidc`
- `terraform/modules/iam/github-ecr-role`
- `terraform/modules/eks` is reserved for later implementation
- `terraform/modules/mq` is reserved for later implementation

The current environment layout is:

- `terraform/env/dev` is the working development root module
- `terraform/env/staging` is reserved for a future staging root module
- `terraform/env/prod` is reserved for a future production root module

## What this foundation will build

The Day 12 foundation defines the Terraform structure that later days will use to provision:

- a VPC with public and private subnets
- ECR repositories for the retail services
- GitHub Actions OIDC trust for AWS authentication
- a GitHub ECR push role scoped to the retail repositories

Later days can extend this structure to support:

- EKS cluster resources
- queueing and messaging resources
- additional shared platform services

## Version constraints

The development root module pins:

- Terraform `>= 1.10.0, < 2.0.0`
- AWS provider `~> 6.0`

These constraints are defined in `terraform/env/dev/versions.tf`.

## Environment strategy

This repository uses separate root modules for each environment instead of mixing all environments into one root:

- `dev`
- `staging`
- `prod`

For Day 12, only `dev` is implemented and validated. `staging` and `prod` remain placeholders so the layout is explicit before more infrastructure is added.

## Module boundaries

### `vpc`

Responsible for:

- VPC creation
- public subnets
- private subnets
- internet gateway
- route tables
- route table associations

Inputs include:

- project name
- environment name
- VPC CIDR
- public subnet map
- private subnet map

Outputs include:

- VPC ID and ARN
- route table IDs
- subnet IDs
- subnet CIDRs

### `ecr`

Responsible for:

- ECR repository creation
- image scanning on push
- immutable image tags
- ECR lifecycle retention rules

Inputs include:

- repository names
- project name
- environment name
- untagged image expiration days
- maximum image count

Outputs include:

- repository URLs
- repository ARNs
- repository names
- registry ID

### `iam/github-oidc`

Responsible for:

- GitHub Actions OIDC provider creation

Output includes:

- OIDC provider ARN

### `iam/github-ecr-role`

Responsible for:

- GitHub Actions IAM role for ECR push
- trust policy limited to the configured repository and branch
- ECR push permissions limited to the target repositories

Inputs include:

- project name
- environment
- GitHub repository
- GitHub branch
- OIDC provider ARN
- ECR repository ARNs

Output includes:

- role ARN

## Naming and tags

The current naming pattern follows:

- `${project_name}-${environment}-vpc`
- `${project_name}-${environment}-public-${subnet_key}`
- `${project_name}-${environment}-private-${subnet_key}`
- `${project_name}-${environment}-igw`
- `${project_name}-${environment}-public-route-table`
- `${project_name}-${environment}-private-route-table`
- `${project_name}-${environment}-github-ecr`

The current default tags applied through the AWS provider are:

- `Project`
- `Environment`
- `ManagedBy=Terraform`
- `Owner`
- `costcenter`

## Region and cost guardrails

The current development region is:

- `us-east-1`

Current cost and safety controls include:

- ECR repositories use immutable tags
- scan on push is enabled
- untagged images expire after 7 days
- image retention keeps only the latest 20 images
- Terraform state and variable files are excluded from Git

## State strategy

For the Day 12 lab foundation, the current workflow uses local Terraform state inside the root module working directory.

This is acceptable for a single-user development lab because:

- the work is still in foundation stage
- only `dev` is actively implemented
- no shared team workflow is using this root module yet

Before shared staging or production use, this configuration should move to a protected remote backend such as:

- S3 for remote state storage
- DynamoDB locking or the modern backend locking mechanism chosen for the platform

The production rule is:

- do not commit state
- do not commit secrets
- do not share local state between environments

## Validation proof

The current Day 12 foundation has been validated locally from `terraform/env/dev` with:

```powershell
terraform fmt -check -recursive .\terraform
Set-Location .\terraform\env\dev
terraform init -backend=false
terraform validate
```

Validation result:

- formatting check passed
- module initialization passed
- Terraform validation passed

## Next action

The next implementation step after this foundation is:

- build the VPC and subnet infrastructure on top of the validated `dev` root module
