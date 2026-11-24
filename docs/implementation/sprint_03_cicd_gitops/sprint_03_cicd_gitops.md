# Sprint 3 - CI/CD, DevSecOps and GitOps

Layer: Required + Upgrade
Target window: Days 2-10
Goal: A push to GitHub builds, scans and ships the UI image; later, ArgoCD (not Jenkins) deploys it from Git.

## Entry Gates
Do not start this sprint just because this file exists. Tick each gate and write date + evidence.

- [ ] Gate A - S1-M02, S1-M03 done (network + ECR) before Jenkins EC2 / first push.
      Verification note: 
- [ ] Gate B - GitOps part (S3-M05/M06) only after S2-M04 passes.
      Verification note: 

## Product Decisions
- Trivy fails the build on CRITICAL (exit code 1).
- Week 1: pipeline may deploy with kubectl set image. After S3-M06 Jenkins never touches the cluster.
- Jenkins authenticates with the LabRole instance profile; the GitHub token lives in Jenkins credentials only.
- Jenkins has an Elastic IP so the webhook URL survives stop/start.

## Delivery Shape
| Mission | Purpose | Owner | Depends on | Layer |
|---|---|---|---|---|
| S3-M01 | Jenkins EC2 module (user-data, LabRole profile, Elastic IP) | B | S1-M02 | base |
| S3-M02 | Jenkinsfile: build, Trivy (fail on CRITICAL), push to ECR | B | S3-M01, S1-M03 | base |
| S3-M03 | GitHub webhook trigger | B | S3-M02 | base |
| S3-M04 | IaC scanning stage (tfsec or checkov) | C | S3-M02 | upgrade |
| S3-M05 | Install ArgoCD and Application for k8s/ | B | S2-M04 | upgrade |
| S3-M06 | Jenkins commits image tag to Git (replace kubectl deploy) | B | S3-M03, S3-M05 | upgrade |

## Mission 'Done when' (this is the Acceptance Checklist for lightweight missions)
- [ ] **S3-M01** Jenkins UI reachable; docker, trivy, kubectl, aws-cli installed; no static AWS keys.
- [ ] **S3-M02** Image lands in ECR; pipeline FAILS on a deliberately vulnerable base image. Temporary kubectl set image deploy stage allowed.
- [ ] **S3-M03** A push to main starts a build automatically.
- [ ] **S3-M04** Pipeline scans terraform/ and fails on HIGH findings (or documented exceptions).
- [ ] **S3-M05** ArgoCD UI shows retail app Synced and Healthy; self-heal reverts a manual kubectl edit.
- [ ] **S3-M06** UI change -> Jenkins -> Git commit -> ArgoCD -> new pod. Jenkins no longer uses kubectl. git revert rolls back.

## Dependency Graph
```text
S3-M01 --> S3-M02 --+--> S3-M03 --+
                    +--> S3-M04    |
S2-M04 --> S3-M05 -------------+--> S3-M06
```

## Cross-Sprint Ownership
| Concern | Owning sprint |
|---|---|
| Cluster + ECR creation | S1 |
| Alerts and dashboards | S4 |

## Sprint Go/No-Go Gate
All must be true. Walked by someone who did NOT write the last mission. Any failure = NO-GO, reported against the owning mission.

- [ ] Webhook-triggered build works end to end.
- [ ] A deliberately vulnerable image is rejected by Trivy.
- [ ] ArgoCD shows retail app Synced/Healthy.
- [ ] A UI change reaches the cluster only via a Git commit made by Jenkins.
- [ ] git revert of that commit rolls the UI back.

## Handoff To Next Sprint
- Pipeline name and Jenkinsfile path.
- ArgoCD Application tracks k8s/ on main.
- Image tag convention: ${BUILD_NUMBER} (or git short SHA) written into k8s/ui kustomization.
