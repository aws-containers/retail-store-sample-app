# Sprint 0 - Team Setup & Conventions

Layer: Foundation
Target window: Day 1
Goal: Everyone can work in the same repo, in their own Learner Lab account, using the same names.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - Team: three members agreed on roles A/B/C (Infra, CI/CD & Security, Observability & Serverless).
      Verification note: 

## Product Decisions
- Git is the shared environment; each student has a separate AWS account. No credentials are ever shared or committed.
- main is always working. Changes only by pull request, reviewed by one other person.
- Commit messages start with the local ID, e.g. 'S1-M02: add network module'.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S0-M01 | Create repo from fork, protect main, add templates | A | Gate only | base |
| S0-M02 | Agree and commit docs/conventions.md | ALL | S0-M01 | base |
| S0-M03 | Prepare each Learner Lab account (LabRole check, $10 budget alert) | ALL | Gate only | base |
| S0-M04 | Run GitHub bootstrap (labels, issues, project board) | A | S0-M01 | base |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S0-M01** Private repo; main protected (or agreed team rule); PR template, CODEOWNERS, issue template merged; all 3 invited.
- [ ] **S0-M02** Names for cluster, namespaces, repos, tables, buckets, region, tags agreed and merged.
- [ ] **S0-M03** Each member comments 'LabRole OK + budget set' on the issue; `aws iam get-role --role-name LabRole` succeeds.
- [ ] **S0-M04** Missions and sprint tracking issues exist; docs/status.md has the mapping.

## Dependency Graph
```text
S0-M01 -> S0-M02 -> S0-M04
S0-M03 (each member, in parallel)
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| AWS resources | S1 |
| Pipelines | S3 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] Repo exists, all 3 members can push branches and open PRs.
- [ ] docs/conventions.md is merged.
- [ ] Each member confirmed LabRole + billing alert in their own account.
- [ ] GitHub labels, missions and sprint tracking issues exist.

## Handoff To Next Sprint
- Repo skeleton and folder ownership (see CODEOWNERS).
- Naming contract in docs/conventions.md.
- Issue board with one issue per mission.
