# Day 13 VPC Evidence

This file captures the proof that the Day 13 VPC and subnet foundation was built and validated for the `dev` environment.

## Terraform checks

Commands run:

```powershell
terraform fmt -check -recursive .\terraform
Set-Location .\terraform\env\dev
terraform validate
terraform plan -no-color
terraform output -json
```

Observed result:

- formatting check passed
- Terraform validation passed
- Terraform plan reported `No changes. Your infrastructure matches the configuration.`

Saved local plan artifact:

- `terraform/env/dev/day13`

## Terraform outputs

- `vpc_id`: `vpc-09b824b204ac8bc6a`
- `vpc_cidr_block`: `10.20.0.0/16`
- `internet_gw_id`: `igw-0d94f551f43b8a833`
- `public_route_table_id`: `rtb-0f3a5b6ca7de76e5e`
- `private_route_table_id`: `rtb-040163bee02f37fce`
- `public_subnet_ids.az1`: `subnet-0488659706053f02b`
- `public_subnet_ids.az2`: `subnet-0314ab26cecaf8b85`
- `private_subnet_ids.az1`: `subnet-0544b5c8239a09a95`
- `private_subnet_ids.az2`: `subnet-0778dc36d2468e355`

## AWS validation

AWS identity used during verification:

- account: `147723036683`
- caller ARN: `arn:aws:iam::147723036683:user/ahmed`

Live AWS checks confirmed:

- VPC `vpc-09b824b204ac8bc6a` exists and is `available`
- VPC CIDR is `10.20.0.0/16`
- `EnableDnsSupport = true`
- `EnableDnsHostnames = true`
- public subnets exist in `us-east-1a` and `us-east-1b`
- private subnets exist in `us-east-1a` and `us-east-1b`
- public subnets have `MapPublicIpOnLaunch = true`
- private subnets have `MapPublicIpOnLaunch = false`
- public subnets have `kubernetes.io/role/elb = 1`
- private subnets have `kubernetes.io/role/internal-elb = 1`
- public route table includes `0.0.0.0/0 -> igw-0d94f551f43b8a833`
- private route table has only the local VPC route
- route table associations are attached to the expected subnets

## Cost and scope note

No NAT gateway is currently created. This matches the Day 13 rule to add NAT only when required and cost-approved.

## Conclusion

The Day 13 network foundation is deployed and validated. The current `dev` Terraform root matches the real AWS infrastructure with no pending changes.
