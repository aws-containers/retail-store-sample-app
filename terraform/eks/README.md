# AWS Containers Retail Sample - EKS Terraform

This Terraform module creates all the necessary infrastructure and deploys the retail sample application on [Amazon Elastic Kubernetes Service](https://aws.amazon.com/eks/) (EKS).

It provides:
- VPC with public and private subnets
- EKS cluster and managed node groups in multiple availability zones
- All application dependencies such as RDS, DynamoDB table, Elasticache etc.

NOTE: This will create resources in your AWS account which will incur costs. You are responsible for these costs, and should understand the resources being created before proceeding.

## Usage

Pre-requisites for this are:
- AWS, Terraform and kubectl installed locally
- AWS CLI configured and authenticated with account to deploy to

After cloning this repository run the following commands:

```shell
cd terraform/eks

terraform init
terraform plan
terraform apply
```

The final command will prompt for confirmation that you wish to create the specified resources. After confirming the process will take at least 15 minutes to complete. You can then retrieve the AWS CLI command needed to configure `kubectl` for the new EKS cluster:

```shell
terraform output -raw configure_kubectl
```

The output will look something like this:

```
aws eks --region us-west-2 update-kubeconfig --name retail-store
```

Run the above command and then test the cluster is accessible:

```shell
kubectl get svc -n ui ui-lb
```

The output will look something like this:

```
NAME    TYPE           CLUSTER-IP      EXTERNAL-IP                                                              PORT(S)        AGE
ui-lb   LoadBalancer   172.20.196.69   aec46b0c98b974cc28201c38dbba79b6-1234567678.us-west-2.elb.amazonaws.com   80:32154/TCP   18h
```

Enter the domain name from the `EXTERNAL-IP` column in a web browser to access the application.

## Reference

This section documents the variables and outputs of the Terraform configuration.

### Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| `environment_name` | Name of the environment which will be used for all resources created | `string` | `retail-store` | yes |
| `kustomization_path` | Path to kustomization that will deploy the sample application in the EKS cluster | `string` | `../../deploy/kubernetes/kustomize/recipes/full` | yes |

### Outputs

| Name | Description |
|------|-------------|
| `configure_kubectl` | AWS CLI command to configure `kubectl` for EKS cluster |