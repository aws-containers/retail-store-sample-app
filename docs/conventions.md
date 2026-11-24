# Conventions (the interface contract)

Change this file only by PR approved by all three members.

| Item | Value |
|---|---|
| AWS region | us-east-1 (change here first if the lab uses another) |
| Cluster name | eks-shopops |
| EKS version | TBD in S1-M04 (newest standard-support version in the lab) |
| Namespaces | retail, monitoring, argocd, ingress-nginx |
| ECR repo (UI) | retail-ui |
| DynamoDB tables | shopops-carts, shopops-incidents |
| Incident bucket | shopops-incidents-<account_id> |
| State bucket | shopops-tfstate-<account_id> |
| Lock | DynamoDB table shopops-tflock (or S3 native lock if supported) |
| IAM role | LabRole only |
| Tag on every resource | Project=shopops, Owner=<member> |
| Image tag | ${BUILD_NUMBER} |
| Branches | main + infra/*, cicd/*, obs/*, docs/* |
| Commit prefix | Local ID, e.g. `S2-M02: connect cart to DynamoDB` |
| Secrets | Jenkins credentials / env only, never Git |

## Roles
| Letter | Name | Owns (see CODEOWNERS) |
|---|---|---|
| A | Infra | terraform/, scripts/, Makefile |
| B | CI/CD & Security | jenkins/, k8s/, argocd/ |
| C | Observability & Serverless | helm-values/, lambda/, dashboards/ |

Docs are shared; every member writes part of the README.

## Daily rules
1. 15-minute sync (or a message): done / blocked / need.
2. Each session ends with `make down` in your own account.
3. Integration account runs the full stack once per week.
