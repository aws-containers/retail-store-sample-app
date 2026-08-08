# Retail Store Sample App: DevOps Study

## Purpose of This Document

This document captures my DevOps understanding of the repository after reviewing its structure, application services, local runtime options, CI/CD workflows, release process, and infrastructure-as-code. It is written as an onboarding and operating guide for someone who wants to build, run, test, package, and deploy this project with a professional DevOps mindset.

## Executive Summary

This repository is a monorepo for an educational retail application maintained around container-first deployment patterns on AWS. It is intentionally composed of multiple services written in different languages so that it can demonstrate:

- multi-service application design
- container builds and multi-architecture publishing
- local development with Docker Compose
- Kubernetes packaging with Helm and Helmfile
- AWS deployments with Terraform for EKS, ECS, and App Runner
- observability with Prometheus-style metrics and OpenTelemetry tracing
- functional testing, load generation, and release automation

The project is clearly optimized for demos, education, and platform experimentation rather than direct production use. The repository itself says so explicitly, and the codebase structure supports that conclusion.

## What This Application Is

At a high level, this is a sample online retail store made of five primary services:

| Service | Language | Main Role | Main Dependency |
| --- | --- | --- | --- |
| UI | Java | Public web frontend and aggregation layer | Calls backend APIs |
| Catalog | Go | Product catalog API | MySQL and optional OpenSearch |
| Cart | Java | Shopping cart API | DynamoDB |
| Orders | Java | Order API and event publishing | PostgreSQL and messaging |
| Checkout | Node.js/NestJS | Checkout workflow state | Redis and Orders API |

There are also supporting pieces:

- `src/e2e`: Cypress end-to-end tests
- `src/load-generator`: Artillery-based synthetic load generator
- `src/recommendations`: OpenAPI contract only, no live implementation
- `terraform/`: infrastructure and deployment code
- `docs/`: feature and architecture documentation
- `scripts/`: packaging, testing, and reporting helpers

## Architecture Understanding

### Functional flow

The normal user flow appears to be:

1. The user lands on the UI service.
2. The UI reads catalog data from the Catalog service.
3. The UI sends cart operations to the Cart service.
4. The UI starts checkout through the Checkout service.
5. The Checkout service persists temporary checkout state in Redis.
6. The Checkout service submits finalized orders to the Orders service.
7. The Orders service stores orders in PostgreSQL and can publish events through RabbitMQ or SQS depending on configuration.

### Service dependency map

- `ui` depends on `catalog`, `cart`, `orders`, and `checkout`
- `catalog` depends on MySQL and optionally OpenSearch
- `cart` depends on DynamoDB
- `orders` depends on PostgreSQL and a messaging provider
- `checkout` depends on Redis and optionally a live Orders endpoint

### Design intent

The project is deliberately over-engineered for learning value. That is not a criticism. It is useful for:

- container demos
- platform engineering labs
- observability exercises
- resilience and chaos testing
- comparing deployment targets across AWS services

It is not shaped like a minimal business application. It is shaped like a teaching platform.

## Repository Structure

The most important directories are:

| Path | What it contains |
| --- | --- |
| `src/ui` | Java frontend, templates, static assets, chat integration, metadata and topology pages |
| `src/catalog` | Go catalog API, sample data loading, MySQL and OpenSearch support |
| `src/cart` | Java cart API with DynamoDB support |
| `src/orders` | Java orders API with PostgreSQL and messaging providers |
| `src/checkout` | NestJS checkout API with Redis persistence |
| `src/app` | Full application Docker Compose, Helmfile, app chart, tracing overlay |
| `src/e2e` | Cypress tests and trace assertions |
| `src/load-generator` | Artillery scenarios for load testing |
| `terraform/eks` | EKS deployment options |
| `terraform/ecs` | ECS/Fargate deployment option |
| `terraform/apprunner` | App Runner deployment option |
| `terraform/lib` | Shared Terraform modules for images, dependencies, VPC, EKS, ECS, App Runner |
| `.github/workflows` | CI, E2E, release, publishing, OSS attribution automation |

## Tooling and Build System

### Monorepo tooling

This repo uses:

- `nx` for a unified build/test/container workflow
- `yarn` 4 at the repository root
- `lefthook` for pre-commit quality gates
- `mise` for developer tool version management

### Managed tool versions

The `.mise.toml` file defines a strong local toolchain baseline:

- Java 21
- Node 22
- Go 1.25
- Maven 3.9
- Terraform 1.14
- kubectl 1.34
- Helm 3.19
- Helmfile 1.2
- AWS CLI
- jq
- tflint
- kind
- tilt

### My recommendation

If you are onboarding to this repo, the cleanest first step is:

```bash
mise install
yarn install
```

That gives you the same baseline expected by CI.

## How the Project Runs Locally

### Option 1: UI only

The simplest path is to run only the UI container. This is useful for smoke testing the frontend and mock-backed flows:

```bash
docker run -it --rm -p 8888:8080 public.ecr.aws/aws-containers/retail-store-sample-ui:1.0.0
```

This is convenient, but it does not validate the full distributed system.

### Option 2: Full local stack with Docker Compose

The local multi-service runtime is assembled in `src/app/docker-compose.yml`, which includes the service-level compose files from:

- `src/ui`
- `src/catalog`
- `src/cart`
- `src/checkout`
- `src/orders`

The full app can be started from the root with:

```bash
DB_PASSWORD='test123' yarn nx compose:up
```

Or directly:

```bash
DB_PASSWORD='test123' docker compose --project-directory src/app up --build --detach --wait --wait-timeout 120
```

Operationally, the local Compose environment does the following:

- exposes UI on `http://localhost:8888`
- exposes backend services on local ports mainly for debugging
- provisions local stateful dependencies like MariaDB, Postgres, Redis, RabbitMQ, DynamoDB Local, and optional OpenSearch
- uses service health checks and startup ordering

### Option 3: Local stack with tracing

There is a tracing overlay file at `src/app/docker-compose.tracing.yml`. This adds:

- OpenTelemetry Collector
- Jaeger
- OTLP environment variables for all app services

This is a strong sign that observability was considered part of the demo platform, not an afterthought.

To use it, the CI workflow combines:

- `src/app/docker-compose.yml`
- `src/app/compose.override.yaml`
- `src/app/docker-compose.tracing.yml`

That setup is used for both Cypress testing and trace verification.

## How Services Are Configured

Each service is configured mainly through environment variables. The repo has done a good job documenting those variables in the service READMEs.

Highlights:

- `ui` can point to live APIs or mocks, switch theme, enable search, and enable a chat provider
- `catalog` supports `in-memory` or `mysql` persistence and optional OpenSearch-backed search
- `cart` supports `in-memory` or `dynamodb`
- `orders` supports `in-memory` or `postgres`, plus `in-memory`, `rabbitmq`, or `sqs` messaging
- `checkout` supports `in-memory` or `redis`, and can call a live Orders API

From a DevOps perspective, this is helpful because the application has been designed to run in multiple deployment modes without code changes, only configuration changes.

## Build, Test, and Packaging Model

### NX targets

The common targets across services are:

- `build`
- `test`
- `test:integration`
- `lint`
- `serve`
- `container`
- `helm`
- `manifest`

Examples:

```bash
yarn nx build ui
yarn nx test orders
yarn nx run-many -t build --projects=tag:service --parallel=1
yarn nx run-many -t container --projects=tag:service
```

### Language-specific implementation details

- Java services use Maven wrapper commands
- Catalog uses Go build and Go tests
- Checkout uses NestJS and Yarn scripts
- E2E uses Cypress
- Load generation uses Artillery

### Sample synchronization

The repo also includes `update-samples` targets for some services so product data and images can be propagated from `samples/` into runtime assets. That is useful for keeping demo content consistent.

## CI/CD Understanding

### Pull request validation

The PR workflow includes:

- semantic pull request title validation
- pre-commit style checks through `lefthook`
- affected project build/test/lint execution through `nx`
- affected container builds for projects tagged as containerized services

This is a good monorepo optimization because it avoids rebuilding everything for every small change.

### End-to-end validation

The E2E workflow is stronger than many sample repos. It covers:

- Docker UI-only test flow
- full Docker Compose stack
- trace assertions after traffic generation
- load generation run
- search-enabled stack test
- Kubernetes deployment on a Kind cluster

This is valuable because it validates the application across several operational modes, not only at unit test level.

### Release flow

Release automation is handled with `release-please` and a reusable artifacts workflow.

The release process does all of the following:

1. creates a release/tag from changes on `main`
2. builds container images for `amd64` and `arm64`
3. pushes architecture-specific images to ECR Public
4. creates and pushes multi-arch manifests
5. packages and pushes Helm charts
6. generates release-ready Docker Compose and Kubernetes manifests
7. uploads those generated artifacts to the GitHub release

This is a mature release shape for a sample application.

## Infrastructure-as-Code Understanding

### Deployment targets

The repository supports several main deployment models:

| Target | Purpose |
| --- | --- |
| `terraform/eks/default` | EKS with AWS-managed dependencies |
| `terraform/eks/minimal` | EKS foundation only, fewer managed dependencies |
| `terraform/ecs/default` | ECS/Fargate deployment with managed dependencies |
| `terraform/apprunner/default` | App Runner per service with VPC egress |

### EKS default

This is the most complete Kubernetes deployment path. It provisions:

- VPC and subnets
- EKS cluster and node groups
- managed backing services like RDS, DynamoDB, ElastiCache, and related dependencies
- Helm-based deployment of the app
- optional ADOT/OpenTelemetry
- optional Istio

This is the strongest path if your goal is to study Kubernetes on AWS with realistic supporting services.

### EKS minimal

This creates the cluster foundation but does not create the full external dependency set. I see this as the best choice if you want to:

- bring your own dependencies
- experiment with in-cluster services
- integrate the sample into an existing platform setup

### ECS default

This deploys the services to ECS on Fargate and uses managed dependencies. The README also mentions:

- ECS Service Connect
- optional OpenTelemetry
- configurable Container Insights

This is a strong option if your focus is AWS managed containers without operating Kubernetes.

### App Runner

This creates one App Runner service per application component and uses VPC egress for private dependencies. The repo itself warns that this Terraform path has intermittent apply failures and may require retrying.

That note is important. As a DevOps engineer, I would treat App Runner support here as useful but less stable than the other deployment options.

## Kubernetes Packaging Understanding

The Kubernetes side is thoughtfully organized:

- each service has its own Helm chart
- `src/app/chart` aggregates service charts as dependencies
- `src/app/helmfile.yaml` deploys the full stack
- `src/app/helmfile.slim.yaml` deploys a reduced stack

The charts expose operational controls like:

- resource requests and limits
- service type and ingress settings
- HPA toggles
- PDB toggles
- Istio support
- OpenTelemetry support
- app endpoint configuration

This is a practical design because it lets the same services be reused in standalone or aggregate deployment modes.

## Observability Understanding

Observability is one of the stronger parts of the repo.

### Metrics

The services expose metrics suitable for Prometheus scraping. The Helm chart docs explicitly show metrics annotations and metrics enablement.

### Tracing

Tracing support exists in multiple layers:

- local Docker tracing overlay with OTel Collector and Jaeger
- E2E trace verification
- optional EKS OpenTelemetry support
- optional ECS OpenTelemetry support

### Demo introspection features

The UI exposes helpful demo/ops pages:

- `/info` for environment and platform metadata
- `/topology` for application topology and health

These are not replacements for real observability tooling, but they are useful for demos, workshops, and operator education.

## Security and Operational Guardrails

The repo includes several positive patterns:

- `lefthook` for formatting and Terraform lint checks
- `renovate` for dependency updates
- `release-please` for consistent releases
- OSS attribution automation
- Semgrep helper script for code scanning
- Trivy helper script for container vulnerability scanning
- many containers use reduced Linux capabilities, read-only filesystems, and `no-new-privileges`
- health checks are widely used in Compose and charts

These are good DevOps habits, even in a sample application.

## Risks, Limits, and Non-Production Realities

From a professional DevOps point of view, these are the most important caveats:

### 1. This is not a production-ready system as-is

The repo explicitly says it is for educational purposes only. I agree with that assessment.

### 2. Some quality gates are intentionally lighter than enterprise standards

Examples:

- some lint targets are placeholders
- some test targets are minimal or delegated to integration/E2E layers
- the recommendations service is only a contract, not a deployed implementation

### 3. Secret handling is demo-friendly

Local workflows rely on inline environment variables like `DB_PASSWORD=test123`. That is fine for demos but not for enterprise environments. A real deployment should use secret managers and environment-specific secret injection.

### 4. Environment promotion is release-oriented, not GitOps-oriented

The repo has strong artifact generation, but I do not see a full GitOps promotion model with environment overlays, approval flows, drift control, and deployment reconciliation.

### 5. App Runner path is explicitly noted as flaky

That should be treated seriously when choosing a target platform.

## What I Think the Project Is Best For

I would recommend this repository for:

- learning AWS container platforms
- platform demos
- Helm and Terraform experimentation
- tracing and observability labs
- resilience testing and chaos demos
- monorepo CI/CD reference patterns

I would not directly recommend it as a production application template without further hardening and simplification.

## What You Should Do Next

This section is the practical answer to "what should I do?" depending on your goal.

### If your goal is to understand the project quickly

Follow this order:

1. Read `README.md`
2. Read service READMEs in `src/ui`, `src/catalog`, `src/cart`, `src/orders`, and `src/checkout`
3. Start the app locally with Docker Compose
4. Run the E2E tests
5. Review the Terraform target that matches your preferred runtime

Suggested commands:

```bash
mise install
yarn install
DB_PASSWORD='test123' yarn nx compose:up
```

Open:

```text
http://localhost:8888
```

Then shut it down:

```bash
DB_PASSWORD='test123' yarn nx compose:down
```

### If your goal is to validate the repo like a DevOps engineer

I would do this sequence:

1. Install toolchain with `mise`
2. Install root dependencies with `yarn install`
3. Run local Compose stack
4. Run Cypress E2E tests
5. Run Kind-based Kubernetes validation
6. Review release workflows and generated artifacts
7. Review Terraform deployment path for your chosen AWS runtime

Useful commands:

```bash
yarn nx run-many -t build --projects=tag:service --parallel=1
DB_PASSWORD='test123' yarn nx compose:up
yarn nx execute e2e
```

For Kubernetes-style validation:

```bash
bash scripts/e2e-kind.sh
```

### If your goal is to deploy to AWS

Pick one target first. Do not start with all of them.

Recommended decision path:

1. Choose `EKS default` if you want the richest Kubernetes learning path.
2. Choose `ECS default` if you want less platform overhead.
3. Choose `EKS minimal` if you want to bring your own dependencies.
4. Choose `App Runner` only if you specifically want to explore that runtime and can tolerate retries.

Then:

1. verify AWS CLI access
2. choose a region and environment name
3. review Terraform variables before apply
4. run `terraform init`
5. run `terraform plan`
6. run `terraform apply`
7. capture outputs such as application URL or kubeconfig command

### If your goal is to adapt this for a more production-like environment

I recommend the following backlog:

1. Introduce environment-specific configuration directories
2. Move all secrets to AWS Secrets Manager or SSM Parameter Store
3. Add CI stages for Semgrep, Trivy, and Terraform validation by default
4. Define a formal image-tagging and environment-promotion strategy
5. Add deployment policy and rollback guidance
6. Add SLOs, alerting rules, and dashboards
7. Review service resource requests/limits based on real load tests
8. Decide whether all five services are truly needed for your use case
9. Add GitOps or another controlled deployment model if multiple environments will exist

## My DevOps Assessment

### Strengths

- clear separation of services and dependencies
- multiple supported deployment targets
- strong release artifact generation
- multi-architecture image publishing
- local, Kubernetes, and AWS deployment stories
- observability support built into the sample
- good use of monorepo automation

### Weaknesses

- not intended for production
- some testing/linting depth is uneven across languages
- no end-to-end production operating handbook yet
- no full environment promotion or GitOps model
- App Runner path is less reliable

### Overall conclusion

This is a strong educational and demo-oriented platform engineering sample. From a DevOps perspective, it is valuable because it touches the full software delivery lifecycle:

- source
- build
- package
- test
- release
- deploy
- observe

That makes it a good study project and a useful internal lab. It should be treated as a foundation for learning and experimentation, not as a finished production blueprint.
