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

After the stack is finished deploying, use `pulumi stack output` to retrieve the Kubeconfig for the newly-created EKS cluster:

```shell
pulumi stack output kubeconfig > kubeconfig
```

You can then use this Kubeconfig with `kubectl` to interact with the EKS cluster in order to view nodes, Pods, Services, Deployments, ConfigMaps, etc.

As an example, here is how to get the DNS name of the load balancer for the UI service:

```shell
KUBECONFIG=kubeconfig kubectl -n ui get svc ui-lb
```

Use the DNS name displayed in the `EXTERNAL-IP` column in your browser to access the application. Be sure to specify `http://` in order to connect; there is no SSL/TLS support currently.
