# Sprint 5 - Resilience, Integration and Delivery

Layer: Required
Target window: Days 10-14
Goal: Prove the system survives load and bad releases, rebuild everything from scratch once, and deliver docs, video and defense.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - S2, S3 (through M06) and S4 (through M04) closed.
      Verification note: 
- [ ] Gate B - Integration account chosen and has enough budget.
      Verification note: 

## Product Decisions
- Integration account = the account with the most budget left; the final demo runs there.
- Cleanup is verified by CLI at the end of every session.
- Stretch items (S5-M03) are cut first if time is short.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S5-M01 | metrics-server + HPA on ui (2-5) + k6 load test | C | S4-M01, S2-M03 | base |
| S5-M02 | Rolling update + rollback demonstration | B | S3-M06 | base |
| S5-M03 | STRETCH: NetworkPolicies or chaos test | B | S2-M04 | stretch |
| S5-M04 | Clean-room integration deploy in integration account | A | S1-M06, S3-M06, S4-M05 | base |
| S5-M05 | Blameless post-mortem (1 page) | C | S5-M01, S4-M05 | base |
| S5-M06 | README, architecture diagram, demo video (5-8 min) | ALL | S5-M04 | base |
| S5-M07 | Defense rehearsal and final destroy verification | ALL | S5-M06 | base |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S5-M01** k6 run scales ui above 2 replicas; Grafana screenshot saved.
- [ ] **S5-M02** Bad image is stopped by readiness probe; rollback via git revert documented with timings.
- [ ] **S5-M03** Optional. Policy blocks an unwanted flow, or pod-kill test shows HPA/alerts reacting.
- [ ] **S5-M04** Fresh checkout + `make up` + pipeline run reproduces the whole stack; total time recorded.
- [ ] **S5-M05** docs/postmortem.md based on a real incident from load/self-healing test.
- [ ] **S5-M06** Stranger can reproduce from README; video recorded.
- [ ] **S5-M07** Each member explains every layer; `make down` verified by CLI in every account.

## Dependency Graph
```text
S5-M01 --+
S5-M02 --+--> S5-M05
S5-M04 --> S5-M06 --> S5-M07
(S5-M03 optional)
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| New features | none - feature freeze on day 12 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] Clean-room `make up` from a fresh checkout reproduces the full stack.
- [ ] HPA scaled ui under k6 load.
- [ ] Rollback demonstrated.
- [ ] README lets a stranger reproduce the project.
- [ ] Post-mortem and demo video done.
- [ ] `make down` verified.

## Handoff To Next Sprint
- Project is ready to be defended.
