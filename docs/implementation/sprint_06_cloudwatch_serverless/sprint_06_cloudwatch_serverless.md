# Sprint 6 - CloudWatch & Serverless Remediation

**Teacher's reference:** PDF Step 6 (3 h)  
**Target window:** Days 5-10  
**Goal:** CloudWatch alarm on node CPU (>75%) -> SNS topic -> Python Lambda (LabRole) -> incident item in DynamoDB `shopops-incidents` + JSON report in S3.

## Entry gate
- [ ] Previous sprint's Go/No-Go list was walked (or the missions this sprint depends on are closed - see Delivery table).

## Decisions already taken for this sprint
- All pieces created by Terraform (module `alerting`), none by console clicking.
- Lambda uses LabRole as execution role.
- Alarm metric: AWS/EC2 CPUUtilization with dimension AutoScalingGroupName = the node group's ASG.

## Delivery table
| Mission | Title | Owner | Estimate | Depends on |
|---|---|---|---|---|
| [S6-M01](s6_m01.md) | Terraform: SNS topic + CloudWatch alarm on node CPU > 75% | C | 1 h | S2-M04 |
| [S6-M02](s6_m02.md) | Python Lambda: write the incident to DynamoDB and a JSON report to S3 | C | 1.5 h | S6-M01, S2-M06 |
| [S6-M03](s6_m03.md) | End-to-end alarm test and CloudWatch vs Prometheus comparison | C | 1 h | S6-M02, S5-M03 |

## Sprint Go/No-Go (walked by someone who did not write the last mission)
- [ ] `aws cloudwatch set-alarm-state` produces one DynamoDB item and one S3 object.
- [ ] A real CPU load eventually triggers the alarm (or documented why not within the session).
- [ ] docs/observability_comparison.md written (CloudWatch vs Prometheus).
