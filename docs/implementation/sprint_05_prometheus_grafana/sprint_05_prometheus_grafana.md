# Sprint 5 - Prometheus & Grafana

**Teacher's reference:** PDF Step 5 (3 h)  
**Target window:** Days 4-9  
**Goal:** kube-prometheus-stack runs in namespace `monitoring` with persistent EBS gp3 volumes, a Kubernetes dashboard, and a tested CrashLoopBackOff alert.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- Installed with Helm using a values file committed in helm-values/.
- Grafana/Prometheus are reached with `kubectl port-forward` (no extra public load balancers).
- Persistence on StorageClass gp3 for both Prometheus and Grafana.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S5-M01](s5_m01.md) | Install kube-prometheus-stack with persistent gp3 volumes | C | 1.5 h | S2-M05, S3-M02 |
| [S5-M02](s5_m02.md) | Prove persistence and import the Kubernetes dashboard | C | 1 h | S5-M01 |
| [S5-M03](s5_m03.md) | PrometheusRule alert for CrashLoopBackOff and test it | C | 1 h | S5-M01, S3-M02 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] Prometheus and Grafana pods Running; PVCs Bound on gp3.
- [ ] Deleting the Grafana pod keeps its dashboards/data.
- [ ] Dashboard JSON exported to dashboards/.
- [ ] A crashing test pod makes the alert go Pending -> Firing.
