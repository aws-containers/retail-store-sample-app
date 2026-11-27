# Sprint 7 - HPA, Resilience & FinOps Cleanup

**Teacher's reference:** PDF Step 7 (2 h)  
**Target window:** Days 9-12  
**Goal:** HPA scales `ui` between 2 and 5 replicas under load, rolling update + rollback are demonstrated, post-mortem written, full cleanup verified.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- metrics-server (EKS add-on) is required for HPA and is NOT installed by default.
- HPA needs CPU `requests` on the ui container.
- Cleanup order is always: delete Services (load balancers) -> helm uninstall -> delete PVCs -> terraform destroy -> CLI verification.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S7-M01](s7_m01.md) | HorizontalPodAutoscaler on `ui` (2 to 5 replicas) | C | 45 min | S3-M04, S2-M05 |
| [S7-M02](s7_m02.md) | Load test and observe autoscaling | C | 1 h | S7-M01, S5-M02 |
| [S7-M03](s7_m03.md) | Rolling update and rollback demonstration | B | 1 h | S4-M05, S3-M04 |
| [S7-M04](s7_m04.md) | Blameless post-mortem (1 page) | C | 1 h | S7-M02 |
| [S7-M05](s7_m05.md) | Final cleanup and CLI-verified zero-cost state | A | 45 min | S2-M07 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] `kubectl get hpa` shows scale-up beyond 2 replicas during the load test.
- [ ] Rollback of a bad release demonstrated and timed.
- [ ] docs/postmortem.md (1 page, blameless) merged.
- [ ] `make down` leaves zero billable resources (CLI evidence saved).
