# Research notes: where we deviate from the PDF, and why

We checked the upstream repository, AWS documentation, the AWS EKS Workshop (which uses the same sample app) and other student projects on GitHub that run EKS inside AWS Academy Learner Lab. Every deviation is small and is listed so the professor can see it was deliberate.

## Corrections / improvements to the PDF

| # | PDF says | Problem found | What we do | Source |
|---|---|---|---|---|
| 1 | EKS version 1.30 | 1.30 is in EXTENDED support: control plane costs $0.60/h instead of $0.10/h | choose the newest STANDARD_SUPPORT version (CLI command in S2-M04) | AWS EKS version lifecycle + EKS pricing pages |
| 2 | main.tf references aws_subnet.public | No VPC/subnet/IGW/route resources are defined | network module (S2-M02) | the PDF itself |
| 3 | DynamoDB carts: only hash key `id` | Upstream/EKS Workshop table also has GSI `idx_global_customerId` on customerId | add the GSI (S2-M06) | eksworkshop.com ACK/Crossplane lab pages |
| 4 | Trivy `--exit-code 0` | Scan can never fail the build | `--severity CRITICAL --ignore-unfixed --exit-code 1` (S4-M03) | Trivy docs practice |
| 5 | HPA 2-5 on ui | EKS has no metrics-server by default; HPA also needs CPU requests | add metrics-server add-on (S2-M05) + requests (S3-M04) | other Learner Lab EKS repos install it |
| 6 | VPC in 2 AZs | us-east-1e cannot host the EKS control plane | pick AZs explicitly (S2-M02) | two Learner Lab EKS repos document this |
| 7 | LoadBalancer Service on ui | Service cannot find subnets without tags | tag subnets kubernetes.io/role/elb and cluster tag | Learner Lab EKS repo (Castor-Garage) |
| 8 | 'LabRole IAM instance profile' for Jenkins | The pre-created profile is named LabInstanceProfile | use `iam_instance_profile = "LabInstanceProfile"` | AWS Academy Learner Lab foundational services doc |
| 9 | (implicit) usual EKS Terraform module | terraform-aws-modules/eks creates IAM roles, blocked in Learner Lab | raw aws_eks_cluster / aws_eks_node_group | madurubini/oficina-infra-k8s and others |
| 10 | Upstream manifest deploys to `default` | PDF wants namespace `retail` | kustomize namespace override (S3-M01) | upstream README |
| 11 | Cart uses DynamoDB | Local setup config contains fake AWS keys + endpoint to a local pod | remove them so the node role is used (S3-M03) | eksworkshop.com ACK lab diff |
| 12 | cleanup.sh 4 lines | Load balancers/EBS created by Kubernetes block VPC deletion and keep billing | ordered cleanup + CLI verification (S2-M07) | statline-infra PR notes (waits for the LB before deleting the VPC) |
| 13 | Everything destroyed each session | Jenkins and its webhook would be reconfigured by hand each time | Jenkins Configuration as Code + webhook update script (S4-M02/M04) | consequence of the PDF's own FinOps rule |

## Open risks (NOT verified for Learner Lab - test early and report)

| Risk | Early test | Owner | Fallback |
|---|---|---|---|
| R1 AWS Budgets may be blocked | S1-M02 | ALL | document the AccessDenied; use the lab budget meter |
| R2 EBS CSI driver on node role (no IRSA) | S2-M05 test PVC | A | tell the professor; ask for guidance |
| R3 Jenkins (LabRole) not authorized in the cluster | S4-M05 spike | B | ServiceAccount-token kubeconfig |
| R4 t3.small too small for the Java build | S4-M03 | B | swap file, then t3.medium |
| R5 2 x t3.medium too small for app + monitoring | S3-M02, S5-M01 | B, C | lower requests/retention, node group max 3 |
| R6 Pods reaching DynamoDB through the node role (hop limit) | S3-M03 | B | test with amazon/aws-cli pod |
| R7 SSM SecureString readable by LabRole | S4-M02 | B | enter token manually each session |

## Sources
- https://github.com/aws-containers/retail-store-sample-app (README: kubernetes.yaml release, `kubectl get svc ui`, terraform/eks/minimal)
- https://eksworkshop.com/docs/fastpaths/eks-capabilities/ack/provision-table and /migrate-carts (cart env vars, DynamoDB table with GSI)
- https://docs.aws.amazon.com/eks/latest/userguide/kubernetes-versions.html and https://aws.amazon.com/eks/pricing/
- AWS Academy Learner Lab foundational services (LabRole / LabInstanceProfile)
- Student projects: github.com/madurubini/oficina-infra-k8s, github.com/15SOAT-JVGG/oficina-infra-k8s, github.com/Castor-Garage/mecanica-k8s-infra-SOAT, github.com/christochula/oficina-infra-kubernetes, github.com/statline-iteso/statline-infra (PR 52)
