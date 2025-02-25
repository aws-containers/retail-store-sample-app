# AWS Containers Retail Sample - EKS Terraform (Minimal)

This Terraform module creates all the necessary infrastructure for the retail sample application on [Amazon Elastic Kubernetes Service](https://aws.amazon.com/eks/) (EKS). This configuration will only provision the EKS cluster and other foundational infrastructure such as the VPC. It will not deploy services to fulfill application dependencies such as Amazon RDS or Amazon DynamoDB (see [default configuration](../default/)).

It provides:

- VPC with public and private subnets
- EKS cluster and managed node groups in multiple availability zones

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
kubectl get nodes
```

## Reference

This section documents the variables and outputs of the Terraform configuration.

### Inputs

| Name                    | Description                                                                                                                                     | Type     | Default        | Required |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | -------- | -------------- | :------: |
| `environment_name`      | Name of the environment which will be used for all resources created                                                                            | `string` | `retail-store` |   yes    |
| `opentelemetry_enabled` | Flag to enable OpenTelemetry, which will install the AWS Distro for OpenTelemetry addon in the EKS cluster and create OpenTelemetry collectors. | `bool`   | `false`        |    no    |
| `istio_enabled`         | Flag to enable Istio, which will install Istio in the EKS cluster                                                                               | `bool`   | `false`        |    no    |

### Outputs

| Name                | Description                                            |
| ------------------- | ------------------------------------------------------ |
| `configure_kubectl` | AWS CLI command to configure `kubectl` for EKS cluster |
