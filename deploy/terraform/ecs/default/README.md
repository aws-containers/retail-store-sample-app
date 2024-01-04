# AWS Containers Retail Sample - ECS Terraform (Default)

This Terraform module creates all the necessary infrastructure and deploys the retail sample application on [Amazon Elastic Container Service](https://aws.amazon.com/ecs/).

It provides:
- VPC with public and private subnets
- ECS cluster using Fargate for compute
- All application dependencies such as RDS, DynamoDB table, Elasticache etc.
- Deployment of application components as ECS services
- ECS Service Connect to handle traffic between services

NOTE: This will create resources in your AWS account which will incur costs. You are responsible for these costs, and should understand the resources being created before proceeding.

## Usage

Pre-requisites for this are:
- AWS, Terraform and kubectl installed locally
- AWS CLI configured and authenticated with account to deploy to

After cloning this repository run the following commands:

```shell
cd terraform/ecs/default

terraform init
terraform plan
terraform apply
```

The final command will prompt for confirmation that you wish to create the specified resources. After confirming the process will take at least 15 minutes to complete. You can then retrieve the HTTP endpoint for the UI from Terraform outputs:

```shell
terraform output -raw application_url
```

Enter the URL in a web browser to access the application.

## Reference

This section documents the variables and outputs of the Terraform configuration.

### Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| `environment_name` | Name of the environment which will be used for all resources created | `string` | `retail-store-ecs` | yes |

### Outputs

| Name | Description |
|------|-------------|
| `application_url` | URL where the application can be accessed |
