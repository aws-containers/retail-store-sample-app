# Sprint 2 - Retail Store on Kubernetes

Layer: Required
Target window: Days 3-6
Goal: The 5 retail microservices run in namespace `retail`, the cart uses DynamoDB, and the UI is reachable from the internet.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - S1-M04 and S1-M07 done (cluster + ingress).
      Verification note: 
- [ ] Gate B - S1-M05 done (DynamoDB table exists).
      Verification note: 

## Product Decisions
- All manifests live in k8s/ in Git; nothing is applied by hand-written ad hoc YAML.
- MySQL/Redis/etc. run in-cluster (upstream 'minimal' mode); only the cart uses DynamoDB.
- Namespace is `retail`.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S2-M01 | Import upstream manifests into k8s/ (kustomize base) | B | S1-M04 | base |
| S2-M02 | Connect cart to DynamoDB | B | S2-M01, S1-M05 | base |
| S2-M03 | Probes, requests/limits, UI exposed via ingress | B | S2-M01, S1-M07 | base |
| S2-M04 | End-to-end smoke test script | B | S2-M02, S2-M03 | base |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S2-M01** 5 services Running in namespace retail, applied from k8s/ only.
- [ ] **S2-M02** Cart item persists in shopops-carts and survives a cart pod restart.
- [ ] **S2-M03** Every deployment has liveness+readiness probes and resources; UI loads from the ingress URL.
- [ ] **S2-M04** scripts/smoke_test.sh browses, adds to cart and checks out with all-200 responses.

## Dependency Graph
```text
S2-M01 --+--> S2-M02 --+
           +--> S2-M03 --+--> S2-M04
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| Image build + push | S3 |
| GitOps sync | S3 |
| Metrics, HPA | S4/S5 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] All pods Running/Ready in retail.
- [ ] Cart item survives a cart pod restart (proves DynamoDB).
- [ ] UI reachable through the ingress URL.
- [ ] smoke_test.sh passes.

## Handoff To Next Sprint
- k8s/ is a self-contained, applyable base (kustomize).
- UI URL discovery command documented.
- Deployment names: ui, catalog, cart, orders, checkout (container names equal deployment names).
