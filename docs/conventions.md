# Conventions (interface contract)

Changes to this file require a PR approved by all three members.

## 1. Technical configuration

| Item | Value |
|---|---|
| AWS region | us-east-1 |
| AZs for subnets | us-east-1a, us-east-1b (never us-east-1e) |
| Cluster name | eks-shopops |
| Kubernetes version | Newest STANDARD_SUPPORT version at build time (fill in S2-M04) |
| Namespaces | retail, monitoring |
| Helm release (monitoring) | monitoring |
| ECR repositories | retail-ui, retail-catalog, retail-cart, retail-orders, retail-checkout |
| DynamoDB tables | shopops-carts (key: id + GSI: idx_global_customerId), shopops-incidents (key: id) |
| Incident bucket | shopops-incidents-<account_id> |
| Terraform state bucket | shopops-tfstate-<account_id> |
| Lock table | shopops-tflock |
| IAM | LabRole (cluster, nodes, Lambda), LabInstanceProfile (Jenkins) |
| Resource tags | Project=shopops, Owner=<member> |
| Image tag | ${BUILD_NUMBER} |
| Branch names | infra/*, app/*, cicd/*, obs/*, docs/* |
| Commit prefix | Local mission ID, e.g. `S2-M04: add EKS module` |
| Secrets | Never commit secrets to Git. Use Jenkins credentials, SSM SecureString, or local environment variables. |
| Tool versions | Terraform ____, AWS CLI ____, kubectl ____, Helm ____ (fill in S1-M01) |

## 2. Team responsibilities

| Member | Role | Main ownership |
|---|---|---|
| A | Infrastructure | terraform/, scripts/, Makefile |
| B | CI/CD & Kubernetes application | jenkins/, k8s/, Jenkinsfile |
| C | Observability & Serverless | helm-values/, monitoring/, lambda/, dashboards/ |

Documentation is shared. Every member contributes to the README.

## 3. Daily coordination

1. Have a 15-minute sync or send a short update covering: done, blocked, and need.
2. Each member ends their own AWS session with `make down`, according to the project cleanup procedure.
3. The integration account runs the complete stack once per week.
4. Communicate blockers early rather than waiting until the next meeting.

## 4. Git workflow

1. Work on your assigned feature branch.
2. Use the mission ID as the commit prefix.
3. Open a pull request against `main`.
4. Request review from the relevant teammate.
5. Do not push directly to `main`.
6. Keep pull requests focused on one mission or closely related changes.

## 5. Infrastructure and security rules

1. Use the agreed AWS region and Availability Zones.
2. Use the Learner Lab-provided IAM roles and instance profile; do not create IAM roles that Learner Lab does not permit.
3. Never commit AWS credentials, tokens, passwords, or private keys.
4. Tag resources consistently.
5. Verify resource cleanup at the end of each AWS session.
