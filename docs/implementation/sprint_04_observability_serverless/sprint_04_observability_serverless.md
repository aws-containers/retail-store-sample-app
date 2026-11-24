# Sprint 4 - Observability and Self-Healing

Layer: Required + Upgrade
Target window: Days 4-10
Goal: We can see the cluster (Prometheus/Grafana), get alerted (CloudWatch -> SNS), and a Lambda records and remediates incidents.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - S1-M07 done (gp3 + cluster) for Prometheus.
      Verification note: 
- [ ] Gate B - S1-M05 done (incidents table + bucket) for Lambda.
      Verification note: 

## Product Decisions
- Lambda runs with LabRole; it only does two actions: record the incident, and scale the node group by +1 up to max 3.
- Every automatic action is written to shopops-incidents with the action taken.
- Alarm/SNS/Lambda are in Terraform (module alerting), never clicked in the console.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S4-M01 | kube-prometheus-stack with gp3 persistence | C | S1-M07 | base |
| S4-M02 | Grafana dashboards + CrashLoopBackOff PrometheusRule | C | S4-M01, S2-M01 | base |
| S4-M03 | CloudWatch CPU alarm -> SNS (Terraform module alerting) | C | S1-M04 | base |
| S4-M04 | Lambda: record incident in DynamoDB + JSON in S3 | C | S4-M03, S1-M05 | base |
| S4-M05 | Self-healing: Lambda scales node group (+1, max 3) | C | S4-M04 | upgrade |
| S4-M06 | CloudWatch vs Prometheus comparison write-up | C | S4-M02, S4-M03 | base |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S4-M01** Prometheus + Grafana Running; PVCs Bound on gp3; data survives pod restart.
- [ ] **S4-M02** Dashboard JSON exported to Git; a crashing test pod fires the alert.
- [ ] **S4-M03** Alarm and topic created by Terraform; set-alarm-state publishes to SNS.
- [ ] **S4-M04** set-alarm-state creates one item in shopops-incidents and one object in S3.
- [ ] **S4-M05** On alarm the node group grows once, action logged in incident record, scale-back documented.
- [ ] **S4-M06** docs/observability_comparison.md reviewed by another member.

## Dependency Graph
```text
S4-M01 --> S4-M02 --+
S4-M03 --> S4-M04 --> S4-M05
                       +--> S4-M06 (also needs S4-M02)
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| HPA and load tests | S5 |
| Cluster creation | S1 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] Prometheus data persists across pod restart.
- [ ] CrashLoopBackOff alert fires on demand.
- [ ] set-alarm-state test creates a DynamoDB item and an S3 JSON object.
- [ ] Self-healing action visible in node count and in the incident record.

## Handoff To Next Sprint
- Grafana dashboard JSON committed.
- Prometheus is the metric source HPA/load tests will be observed with.
- Incident record schema documented in lambda/README.md.
