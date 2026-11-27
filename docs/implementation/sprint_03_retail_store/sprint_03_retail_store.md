# Sprint 3 - Retail Store & DynamoDB

**Teacher's reference:** PDF Step 3 (2.5 h)  
**Target window:** Days 3-6  
**Goal:** 5 microservices run in namespace `retail`, the UI is public through a LoadBalancer Service, and the cart stores data in the DynamoDB table.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- Manifests come from the upstream release kubernetes.yaml, pinned to a version, applied with kustomize from k8s/.
- Namespace `retail`.
- catalog/orders/checkout use in-cluster databases (upstream defaults); ONLY the cart uses real DynamoDB.
- ui is exposed with Service type=LoadBalancer (no ingress controller).

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S3-M01](s3_m01.md) | Import the upstream manifests into k8s/ with kustomize (namespace retail) | B | 45 min | S2-M04 |
| [S3-M02](s3_m02.md) | Deploy the 5 services and expose `ui` with a LoadBalancer | B | 1 h | S3-M01, S2-M05 |
| [S3-M03](s3_m03.md) | Connect the cart service to the DynamoDB table | B | 1 h | S3-M02, S2-M06, S2-M04 |
| [S3-M04](s3_m04.md) | Liveness/readiness probes and resource requests/limits | B | 1 h | S3-M02 |
| [S3-M05](s3_m05.md) | End-to-end smoke test script | B | 45 min | S3-M03, S3-M04 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] All pods Running/Ready in retail.
- [ ] UI opens in a browser through the LoadBalancer DNS name.
- [ ] A cart item appears in the DynamoDB table and survives a cart pod restart.
- [ ] Every deployment has liveness + readiness probes and resource requests/limits.
- [ ] scripts/smoke_test.sh passes.
