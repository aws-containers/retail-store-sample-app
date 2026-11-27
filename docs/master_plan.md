# ShopOps - Master Plan (v2: exactly what the PDF asks)

## Goal
Deliver the project described in the professor's PDF: an automated, observable deployment of the AWS Retail Store sample app on Amazon EKS in AWS Academy Learner Lab, built by 3 students in 2 weeks, with Terraform, Jenkins (Docker, Trivy, ECR), Prometheus/Grafana, CloudWatch + SNS + Lambda, DynamoDB and HPA.

## Sprint sequence (one sprint per step of the PDF, plus documentation)
| Sprint | Teacher's step | Name | Goal |
|---|---|---|---|
| S1 | PDF Step 1 (1 h) | Initialization & FinOps | Every member can talk to their own Learner Lab account, and the Terraform remote state exists. |
| S2 | PDF Step 2 (3 h) | Terraform Foundation (VPC, EKS, ECR, data) | `make up` builds VPC + EKS (2 t3.medium nodes, LabRole) + ECR + DynamoDB/S3 in one command and `make down` removes everything. |
| S3 | PDF Step 3 (2.5 h) | Retail Store & DynamoDB | 5 microservices run in namespace `retail`, the UI is public through a LoadBalancer Service, and the cart stores data in the DynamoDB table. |
| S4 | PDF Step 4 (3.5 h) | Jenkins CI/CD Pipeline | A push to GitHub triggers Jenkins: Docker build -> Trivy scan -> push to ECR -> deploy to EKS -> rollout verified. |
| S5 | PDF Step 5 (3 h) | Prometheus & Grafana | kube-prometheus-stack runs in namespace `monitoring` with persistent EBS gp3 volumes, a Kubernetes dashboard, and a tested CrashLoopBackOff alert. |
| S6 | PDF Step 6 (3 h) | CloudWatch & Serverless Remediation | CloudWatch alarm on node CPU (>75%) -> SNS topic -> Python Lambda (LabRole) -> incident item in DynamoDB `shopops-incidents` + JSON report in S3. |
| S7 | PDF Step 7 (2 h) | HPA, Resilience & FinOps Cleanup | HPA scales `ui` between 2 and 5 replicas under load, rolling update + rollback are demonstrated, post-mortem written, full cleanup verified. |
| S8 | PDF Deliverable 7 | Documentation & Defense | A stranger can reproduce the project from the README; demo video recorded; the team is ready for the 15-minute defense. |

## Who owns what
| Letter | Role | Main missions |
|---|---|---|
| A | Infra | S1-M03, all of S2, S7-M05, S8-M01 |
| B | CI/CD & Kubernetes app | all of S3, all of S4, S7-M03 |
| C | Observability & Serverless | all of S5, all of S6, S7-M01/02/04 |
| ALL | everyone | S1-M01/02, S8-M02/03/04 |

## Suggested 14-day schedule
| Days | A | B | C |
|---|---|---|---|
| 1 | S1 (all) + S2-M01 | S1 + read upstream manifests, S3-M01 | S1 + write Lambda code locally (S6-M02 prep) |
| 2-3 | S2-M02..M06 | S4-M01 (needs network), S3-M01 | S6-M01 draft, S5 values file draft |
| 3-4 | S2-M07, support | S3-M02..M04 (needs cluster) | S5-M01 |
| 5-6 | S1-M03 fixes, reviews | S4-M02, S4-M03 | S5-M02, S5-M03 |
| 7 | **Integration day**: everyone merges, full `make up` in one account, fix mismatches | | |
| 8-9 | reviews, S3-M05 helper | S4-M04, S4-M05 (start spike early!) | S6-M01..M03 |
| 10-11 | support | S7-M03 | S7-M01, S7-M02 |
| 12 | S7-M05, S8-M01 | feature freeze | S7-M04 |
| 13-14 | S8 (all together) | | |

Waiting for the cluster is normal: B and C prepare files and test pieces locally while A delivers S2-M04 (cluster creation takes 10-15 min per attempt).

## Global contracts
See `docs/conventions.md` (names of cluster, namespaces, tables, bucket, repos).

## Non-negotiable decisions
- Stay inside the PDF's scope until Definition of Done is met. Extra features are parked (below).
- Only LabRole / LabInstanceProfile; no IAM resources in Terraform; zero AWS keys in Git.
- Every session ends with `make down` and CLI verification.
- Changes only through reviewed pull requests; one mission per branch.
- Feature freeze on day 12.

## Parked ideas (only after Definition of Done, if time remains)
GitOps with ArgoCD, self-healing Lambda actions, canary releases, chaos tests. Not part of any current issue.

## Explicit non-goals
Multi-region, custom domain/TLS, service mesh, RDS, IRSA/Pod Identity (not possible without IAM role creation).

## Definition of Overall Done (the PDF's 7 deliverables)
- [ ] 1 Infrastructure as Code: VPC without NAT, EKS + 2 t3.medium with LabRole, S3/DynamoDB remote state, clean apply/destroy (S1-S2)
- [ ] 2 Jenkins CI/CD: webhook, build, Trivy, ECR push, EKS deploy, rollout verification (S4)
- [ ] 3 Kubernetes: 5 services, probes, rolling update/rollback, HPA (S3, S7)
- [ ] 4 Observability: Prometheus + Grafana on gp3, exported dashboard JSON, tested alert, CloudWatch comparison (S5, S6)
- [ ] 5 Managed/serverless: DynamoDB carts connected, CloudWatch -> SNS -> Lambda -> DynamoDB incidents + S3 archive (S3, S6)
- [ ] 6 Security & FinOps: LabRole only, no keys in Git, restricted ports, CLI-verified cleanup (all)
- [ ] 7 Documentation & defense: README, 5-8 min video, 1-page post-mortem, 15 min defense (S7, S8)
