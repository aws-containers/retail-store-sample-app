# Deploying with Pulumi

To deploy this application and its associated infrastructure with Pulumi, you should:

* have the Pulumi CLI installed; 
* have NodeJS installed; and
* have the AWS CLI installed and configured for your AWS account.

Then follow these steps:

1. Clone this repository to your local system (if you haven't already).
2. Switch into the `infra` directory.
3. Run `npm install` to install all necessary dependencies.
4. Run `pulumi stack init <name>` to create a new stack.
5. Set your desired AWS region with `pulumi config set aws:region <region-name>`.
6. Run `pulumi up`.

After the stack is finished deploying, use `pulumi stack output` to retrieve the Kubeconfig for the newly-created EKS cluster. Use this with `kubectl` to retrieve the DNS name of the load balancer created for the UI service. Use this DNS name in your browswer to access the application.
