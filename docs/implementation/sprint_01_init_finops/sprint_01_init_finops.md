# Sprint 1 - Initialization & FinOps

**Teacher's reference:** PDF Step 1 (1 h)  
**Target window:** Day 1  
**Goal:** Every member can talk to their own Learner Lab account, and the Terraform remote state exists.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- Each student uses their OWN Learner Lab account; nothing AWS-side is shared.
- Region us-east-1.
- Credentials = Learner Lab temporary keys (access key + secret + SESSION TOKEN) in ~/.aws/credentials, refreshed every session. Never committed.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S1-M01](s1_m01.md) | Set up tools and Learner Lab access (each member) | ALL | 1 h each | - |
| [S1-M02](s1_m02.md) | Billing alert and FinOps rules | ALL | 30 min each | S1-M01 |
| [S1-M03](s1_m03.md) | Terraform remote state: S3 bucket + DynamoDB lock table | A | 45 min | S1-M01 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] Each member: `aws sts get-caller-identity` and `aws iam get-role --role-name LabRole` succeed.
- [ ] Tools installed and versions recorded in docs/conventions.md.
- [ ] Budget alert created OR the AccessDenied evidence recorded.
- [ ] State bucket + lock table exist in the integration account and `terraform init` works against them.
