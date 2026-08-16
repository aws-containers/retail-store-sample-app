# Day 14 ECR, IAM, and OIDC Evidence

This file captures the proof that the Day 14 registry and GitHub-to-AWS identity foundation was built and validated for the `dev` environment.

## Terraform checks

Commands run:

```powershell
terraform fmt -check -recursive .\terraform
Set-Location .\terraform\env\dev
terraform validate
terraform plan -no-color
terraform output -json
terraform state list
```

Observed result:

- formatting check passed
- Terraform validation passed
- Terraform plan reported `No changes. Your infrastructure matches the configuration.`
- Terraform outputs include ECR repository URLs and ARNs, the GitHub OIDC provider ARN, and the GitHub ECR role ARN

Saved local plan artifact:

- `terraform/env/dev/tfplan`

## Terraform outputs

- `github_oidc_provider_arn`: `arn:aws:iam::147723036683:oidc-provider/token.actions.githubusercontent.com`
- `github_ecr_role_arn`: `arn:aws:iam::147723036683:role/retail-store-sample-app-dev-github-ecr-role`

ECR repository URLs:

- `retail-ui`: `147723036683.dkr.ecr.us-east-1.amazonaws.com/retail-ui`
- `retail-carts`: `147723036683.dkr.ecr.us-east-1.amazonaws.com/retail-carts`
- `retail-catalog`: `147723036683.dkr.ecr.us-east-1.amazonaws.com/retail-catalog`
- `retail-checkout`: `147723036683.dkr.ecr.us-east-1.amazonaws.com/retail-checkout`
- `retail-orders`: `147723036683.dkr.ecr.us-east-1.amazonaws.com/retail-orders`

## AWS validation

AWS identity used during verification:

- account: `147723036683`
- caller ARN: `arn:aws:iam::147723036683:user/ahmed`

### ECR repositories

Verified repositories exist:

- `retail-ui`
- `retail-carts`
- `retail-catalog`
- `retail-checkout`
- `retail-orders`

Verified ECR configuration:

- image tag mutability is `IMMUTABLE`
- image scanning on push is enabled
- encryption type is `AES256`
- Terraform state contains one lifecycle policy per repository

### GitHub OIDC provider

Verified OIDC provider:

- URL: `token.actions.githubusercontent.com`
- client ID list includes only `sts.amazonaws.com`

### GitHub ECR IAM role

Verified IAM role:

- role name: `retail-store-sample-app-dev-github-ecr-role`
- trust principal is the GitHub OIDC provider ARN
- trust action is `sts:AssumeRoleWithWebIdentity`

Verified trust restrictions:

- audience condition requires `token.actions.githubusercontent.com:aud = sts.amazonaws.com`
- repository and branch condition requires `token.actions.githubusercontent.com:sub = repo:ahmedmohsen5/retail-store-sample-app:ref:refs/heads/develop`

This proves:

- the approved GitHub identity for the `develop` branch is the intended principal
- other branches are not included in the trust condition and should not be able to assume the role

### IAM permissions review

Verified IAM policy shape:

- `ecr:GetAuthorizationToken` uses `Resource = "*"` which is required by AWS for that action
- repository-scoped actions are limited to the five retail ECR repository ARNs
- the role does not use broad repository wildcards for push actions

Repository-scoped actions granted:

- `ecr:BatchCheckLayerAvailability`
- `ecr:BatchGetImage`
- `ecr:CompleteLayerUpload`
- `ecr:DescribeImages`
- `ecr:DescribeRepositories`
- `ecr:GetDownloadUrlForLayer`
- `ecr:InitiateLayerUpload`
- `ecr:ListImages`
- `ecr:PutImage`
- `ecr:UploadLayerPart`

## Access key review

The Terraform design for image publishing uses GitHub OIDC and a dedicated IAM role instead of long-lived AWS access keys.

The repository CI workflow still contains dummy values like `AWS_ACCESS_KEY_ID: test` for service-level test execution, but those are not real GitHub AWS publish credentials and are not used for ECR release authentication in this Day 14 setup.

## Dependency gate

No ECR publish pipeline was validated as part of this task. The infrastructure and identity pieces were checked independently first, which matches the Day 14 dependency gate.

## Conclusion

The Day 14 registry and identity foundation is deployed and validated. The current `dev` Terraform root matches real AWS infrastructure with no pending changes, and the ECR/OIDC/IAM design is ready for the next pipeline stage.
