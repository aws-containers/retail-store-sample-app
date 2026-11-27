# Sprint 4 - Jenkins CI/CD Pipeline

**Teacher's reference:** PDF Step 4 (3.5 h)  
**Target window:** Days 2-8  
**Goal:** A push to GitHub triggers Jenkins: Docker build -> Trivy scan -> push to ECR -> deploy to EKS -> rollout verified.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- Jenkins runs on EC2 t3.small with instance profile LabInstanceProfile (contains LabRole): no static AWS keys anywhere.
- Everything is destroyed each session, so Jenkins must rebuild itself from code (Jenkins Configuration as Code) and the webhook URL is updated by script.
- The pipeline builds only the `ui` service (as in the PDF).
- Trivy fails the build on fixable CRITICAL vulnerabilities.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S4-M01](s4_m01.md) | Jenkins EC2 instance via Terraform (t3.small, LabInstanceProfile, user-data) | B | 1.5 h | S2-M02 |
| [S4-M02](s4_m02.md) | Jenkins reproducible setup (Configuration as Code + plugins) | B | 1.5 h | S4-M01 |
| [S4-M03](s4_m03.md) | Jenkinsfile: Docker build -> Trivy scan -> push to ECR | B | 1.5 h | S4-M02, S2-M03 |
| [S4-M04](s4_m04.md) | GitHub webhook trigger (and a script to refresh it each session) | B | 1 h | S4-M03 |
| [S4-M05](s4_m05.md) | Deploy stage: Jenkins updates the cluster and checks the rollout | B | 1.5 h | S4-M03, S3-M02 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] Push to main starts a build with no manual click.
- [ ] A deliberately vulnerable image makes the Trivy stage fail.
- [ ] A good build ends with `rollout status` success and the new image tag running in the cluster.
- [ ] Fresh `make up` gives a working Jenkins without manual UI configuration.
