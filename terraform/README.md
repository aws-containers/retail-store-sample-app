# Terraform Overview

This `terraform/` directory is currently a scaffold for a newer layout. The complete, working Terraform implementation in this repository lives in the sibling `terraform2/` directory.

If you want to understand or run the existing infrastructure code today, start with `terraform2/`.

## What `terraform2/` contains

The `terraform2/` folder is organized into deployable stacks and shared library modules.

### Deployable stacks

- `terraform2/apprunner/default/`
  - Deploys the retail sample application to AWS App Runner.
  - Creates a VPC, shared backing services, and one App Runner service per application component.
  - Exposes the UI publicly while keeping backend connectivity inside the VPC.

- `terraform2/ecs/default/`
  - Deploys the application to Amazon ECS on Fargate.
  - Creates the VPC, ECS cluster, shared dependencies, and ECS services for the application.
  - Includes optional OpenTelemetry support, Container Insights settings, and lifecycle event logging.

- `terraform2/eks/default/`
  - Full Amazon EKS deployment for the sample application.
  - Provisions the VPC, EKS cluster, managed node groups, shared dependencies, and Kubernetes/Helm resources for the app.
  - Supports optional OpenTelemetry and Istio integration.
  - Contains a `values/` folder with service-specific Helm values for `assets`, `carts`, `catalog`, `checkout`, `orders`, `ui`, and OpenTelemetry.

- `terraform2/eks/minimal/`
  - Minimal EKS foundation.
  - Provisions the VPC and EKS cluster, but does not create the managed application dependencies such as RDS, DynamoDB, ElastiCache, OpenSearch, or Amazon MQ.

### Shared library modules

- `terraform2/lib/apprunner/`
  - Reusable App Runner module that defines the application services and networking integration.

- `terraform2/lib/ecs/`
  - Reusable ECS module for the application.
  - Includes cluster, ALB, EventBridge, service definitions, and the nested `service/` submodule.

- `terraform2/lib/eks/`
  - Reusable EKS module.
  - Defines the cluster, managed node groups, add-ons, ADOT integration, and optional Istio setup.

- `terraform2/lib/dependencies/`
  - Central module for backing services used by the retail app.
  - Provisions resources such as:
    - Catalog database
    - Orders database
    - Carts DynamoDB table
    - Checkout ElastiCache Redis
    - Catalog OpenSearch
    - Amazon MQ

- `terraform2/lib/vpc/`
  - Shared VPC module.
  - Builds a 3-AZ network layout with public and private subnets, NAT gateway, DNS hostnames, and standard tagging.

- `terraform2/lib/images/`
  - Central image-resolution module.
  - Produces default container image URLs and supports overriding registry, tag, or specific images.

- `terraform2/lib/tags/`
  - Shared tagging helper used across stacks.

## File layout pattern

Most Terraform stacks and modules in `terraform2/` follow a consistent structure:

- `main.tf`: main resources and module wiring
- `variables.tf`: input variables
- `output.tf` or `outputs.tf`: exported values
- `versions.tf`: Terraform and provider version constraints

Some stacks also split concerns into focused files such as:

- `data.tf`
- `iam.tf`
- `sg.tf`
- `kubernetes.tf`
- `opentelemetry.tf`

## Nx project metadata

Each deployable stack in `terraform2/` includes a `project.json` file so it can be managed through Nx. These project definitions use simple Terraform commands such as:

- `terraform init`
- `terraform validate`

One thing to be aware of: some `project.json` files and embedded README examples still reference paths under `terraform/...`, but the checked-in implementation currently exists under `terraform2/...`.

## Current state of `terraform/`

The `terraform/` folder itself is not yet equivalent to `terraform2/`. Right now it contains:

- `env/prod/` and `env/stage/`
  - Each has `main.tf`, `output.tf`, and `variabels.tf`, but those files are currently empty.

- `modules/ecr/`
  - Scaffolded module with empty Terraform files.

- `modules/vpc/`
  - Scaffolded module with starter `main.tf` and `variabels.tf`, while other files are still empty.

## Recommendation

Use `terraform2/` as the source of truth for understanding the existing Terraform architecture in this repository. Treat `terraform/` as an in-progress structure unless it is expanded to match the modules and environments already implemented in `terraform2/`.
