# ShopOps - Master Plan

## Goal
Deliver an automated, observable, reproducible deployment of the AWS Retail Store sample app on Amazon EKS, inside AWS Academy Learner Lab, built by 3 students in 2 weeks. A push to GitHub should lead to a scanned image, a GitOps deployment, visible metrics, and automated incident handling, and the whole stack must be rebuildable from Git in any of our three accounts.

## Layers
| Layer | Meaning |
|---|---|
| Required | Everything the professor's rubric asks for (7 steps / 7 criteria). Must be finished first. |
| Upgrade | Our extra value: ArgoCD GitOps, strict DevSecOps (Trivy blocking + IaC scan), self-healing Lambda. |
| Stretch | Only if time remains: NetworkPolicies or chaos test. First thing cut. |

## Sprint Sequence
| Sprint | Name | Depends on | One-line goal |
|---|---|---|---|
| S0 | Team Setup & Conventions | - | Everyone can work in the same repo, in their own Learner Lab account, using the same names. |
| S1 | Infrastructure as Code | S0 | `make up` creates a working VPC + EKS + ECR + data layer in any Learner Lab account; `make down` removes everything. |
| S2 | Retail Store on Kubernetes | S1 (cluster, ingress, data) | The 5 retail microservices run in namespace `retail`, the cart uses DynamoDB, and the UI is reachable from the internet. |
| S3 | CI/CD, DevSecOps and GitOps | S1 (network, ECR); GitOps part needs S2 | A push to GitHub builds, scans and ships the UI image; later, ArgoCD (not Jenkins) deploys it from Git. |
| S4 | Observability and Self-Healing | S1 | We can see the cluster (Prometheus/Grafana), get alerted (CloudWatch -> SNS), and a Lambda records and remediates incidents. |
| S5 | Resilience, Integration and Delivery | S2, S3, S4 | Prove the system survives load and bad releases, rebuild everything from scratch once, and deliver docs, video and defense. |

## Global Contracts
Decided once, reused everywhere. Details in `docs/conventions.md`.
- Naming: cluster, namespaces, ECR repos, tables, buckets, tags.
- Terraform structure: modules in `terraform/modules`, composition in `terraform/envs/lab`.
- `make up` / `make down` are the only way to create/destroy an environment.
- No AWS keys, kubeconfigs or tfstate in Git. Only LabRole.
- Definition of "done" for a mission: its Done-when box ticked, PR reviewed by another member, tested in a real account, docs updated.

## Non-Negotiable Decisions
- Git is the shared environment; every student has an independent AWS account.
- Base before upgrades: no upgrade mission starts before its base counterpart works (e.g. GitOps after the kubectl deploy works).
- Feature freeze on day 12.
- Everything is destroyed at the end of each Lab session.

## Explicit Non-Goals
- Multi-region, production-grade HA, custom domain/TLS, service mesh.
- RDS (databases stay in-cluster to save budget).
- IRSA / Pod Identity / AWS Load Balancer Controller (not possible without IAM role creation in Learner Lab).

## Definition Of Overall Done
- [ ] Clean-room deploy from a fresh checkout works (S5-M04).
- [ ] All Required missions closed; Upgrade missions closed or explicitly documented as cut.
- [ ] README lets a stranger reproduce the project.
- [ ] Demo video, post-mortem, exported Grafana dashboard committed.
- [ ] Every member can explain every layer.
- [ ] Verified by CLI that all accounts are clean.
