# Sprint 1 - Infrastructure as Code

Layer: Required
Target window: Days 1-4
Goal: `make up` creates a working VPC + EKS + ECR + data layer in any Learner Lab account; `make down` removes everything.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - S0 closed (Go/No-Go walked).
      Verification note: 
- [ ] Gate B - Terraform and AWS CLI installed locally for the owner; `aws sts get-caller-identity` works with current Lab credentials.
      Verification note: 

## Product Decisions
- Public subnets only, no NAT Gateway (cost).
- All roles reuse LabRole; no IAM role is created by Terraform.
- State bucket name = shopops-tfstate-<account_id>.
- Modules live in terraform/modules; terraform/envs/lab only composes them.
- Node metadata hop limit = 2 so pods can use the node role (needed for DynamoDB from pods).
- EKS version: newest standard-support version offered in the lab on the day; record it in docs/conventions.md.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S1-M01 | Terraform bootstrap: state bucket + lock + backend config | A | S0-M03 | base |
| S1-M02 | Network module (VPC, 2 public subnets, IGW, routes) | A | S1-M01 | base |
| S1-M03 | ECR module | A | S1-M01 | base |
| S1-M04 | EKS module (cluster, 2 t3.medium nodes, EBS CSI, hop limit 2) | A | S1-M02 | base |
| S1-M05 | Data module (DynamoDB carts + incidents, S3 incidents bucket) | A | S1-M01 | base |
| S1-M06 | make up / make down / cleanup script | A | S1-M04, S1-M05 | base |
| S1-M07 | gp3 StorageClass + ingress-nginx | A | S1-M04 | base |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S1-M01** `make bootstrap` is idempotent and creates shopops-tfstate-<account_id>; works in a second account.
- [ ] **S1-M02** VPC 10.0.0.0/16, 2 subnets in 2 AZs, IGW, EKS subnet tags, no NAT.
- [ ] **S1-M03** retail-ui repository exists (force_delete true); URL is a terraform output.
- [ ] **S1-M04** `kubectl get nodes` shows 2 Ready; ebs-csi add-on ACTIVE; LabRole used for cluster and nodes.
- [ ] **S1-M05** Tables shopops-carts and shopops-incidents and the S3 bucket exist with agreed names.
- [ ] **S1-M06** Fresh account: `make up` -> ready. `make down` -> CLI check shows no EKS, no LB, no volumes, no EC2 left.
- [ ] **S1-M07** Test PVC binds on gp3; ingress-nginx exposes one load balancer.

## Dependency Graph
```text
S1-M01
  |
  +--> S1-M02 --> S1-M04 --> S1-M07
  +--> S1-M03            |
  +--> S1-M05            +--> S1-M06 (also needs S1-M05)
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| Kubernetes manifests | S2 |
| Jenkins EC2 instance | S3 |
| CloudWatch alarm + SNS | S4 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] Clean `make up` from a fresh checkout reaches `kubectl get nodes` = 2 Ready.
- [ ] ECR repo, DynamoDB tables and S3 bucket exist with the agreed names.
- [ ] PVC on gp3 binds.
- [ ] `make down` leaves no billable resources (verified by CLI).
- [ ] tfsec/checkov not required yet (S3-M04).

## Handoff To Next Sprint
- Cluster eks-shopops reachable via `aws eks update-kubeconfig`.
- StorageClass gp3 and ingress-nginx present.
- Outputs: ecr repo URLs, table names, bucket name, subnet IDs (terraform output).
- `make up` / `make down` are the only supported ways to create/destroy.
