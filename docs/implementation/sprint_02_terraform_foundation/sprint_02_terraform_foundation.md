# Sprint 2 - Terraform Foundation (VPC, EKS, ECR, data)

**Teacher's reference:** PDF Step 2 (3 h)  
**Target window:** Days 1-4  
**Goal:** `make up` builds VPC + EKS (2 t3.medium nodes, LabRole) + ECR + DynamoDB/S3 in one command and `make down` removes everything.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- Plain aws_* Terraform resources, NOT terraform-aws-modules/eks (that module creates IAM roles, which Learner Lab blocks).
- LabRole is cluster role AND node role. Terraform creates no IAM resources.
- Public subnets only, no NAT Gateway.
- Subnets in explicitly chosen AZs (us-east-1e cannot host the EKS control plane).
- Kubernetes version = newest STANDARD_SUPPORT version at build time (older versions bill $0.60/h instead of $0.10/h).
- Our own modules in terraform/modules/{network,ecr,eks,data,...}; terraform/envs/lab composes them.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S2-M01](s2_m01.md) | Terraform skeleton: providers, versions, module layout | A | 45 min | S1-M03 |
| [S2-M02](s2_m02.md) | Network module: VPC 10.0.0.0/16, 2 public subnets, IGW, routes | A | 45 min | S2-M01 |
| [S2-M03](s2_m03.md) | ECR module: repositories for the retail services | A | 30 min | S2-M01 |
| [S2-M04](s2_m04.md) | EKS module: cluster + 2 t3.medium managed nodes with LabRole | A | 1.5 h | S2-M02 |
| [S2-M05](s2_m05.md) | EKS add-ons (EBS CSI, metrics-server) + gp3 StorageClass | A | 1 h | S2-M04 |
| [S2-M06](s2_m06.md) | Data module: DynamoDB (carts, incidents) + S3 incident bucket | A | 45 min | S2-M01 |
| [S2-M07](s2_m07.md) | `make up`, `make down` and cleanup script with CLI verification | A | 1 h | S2-M04, S2-M05, S2-M06 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] Clean `make up` -> `kubectl get nodes` shows 2 Ready.
- [ ] EBS CSI add-on and metrics-server ACTIVE; test PVC on gp3 binds and a pod writes to it.
- [ ] ECR repos, DynamoDB tables (carts with GSI, incidents) and S3 bucket exist.
- [ ] `make down` -> CLI check shows no cluster, no load balancer, no leftover EBS volumes.
