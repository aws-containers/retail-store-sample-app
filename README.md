# AWS Containers Retail Sample

This is a sample application designed to illustrate various concepts related to containers on AWS. It presents a sample retail store application including a product catalog, shopping cart and checkout.

It provides:
- A distributed component architecture in various languages and frameworks
- Utilization of a variety of different persistence backends for different components like MySQL, DynamoDB and Redis
- The ability to run in various container orchestration technologies like Docker Compose, Kubernetes etc.
- Pre-built containers image for both x86-64 and ARM64 CPU architectures
- All components instrumented for Prometheus metrics and OpenTelemetry OTLP tracing
- Support for Istio on Kubernetes
- Load generator which exercises all of the infrastructure

**This project is intended for educational purposes only and not for production use.**

![Screenshot](/docs/images/screenshot.png)

## Application Architecture

The application has been deliberately over-engineered to generate multiple de-coupled components. These components generally have different infrastructure dependencies, and may support multiple "backends" (example: Carts service supports MongoDB or DynamoDB).

![Architecture](/docs/images/architecture.png)

| Component | Language | Container Image     | Description                                                                 |
|-----------|----------|---------------------|-----------------------------------------------------------------------------|
| ![ui workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-ui.yml/badge.svg)        | Java     | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-ui)       | Aggregates API calls to the various other services and renders the HTML UI. |
| ![catalog workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-catalog.yml/badge.svg)   | Go       | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-catalog)  | Product catalog API                                                         |
| ![cart workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-cart.yml/badge.svg)   | Java     | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-cart)     | User shopping carts API                                                     |
| ![orders workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-orders.yml/badge.svg)  | Java     | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-orders)   | User orders API                                                             |
| ![checkout workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-checkout.yml/badge.svg) | Node     | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-checkout) | API to orchestrate the checkout process                                     |
| ![assets workflow](https://github.com/aws-containers/retail-store-sample-app/actions/workflows/ci-assets.yml/badge.svg)  | Nginx    | [Link](https://gallery.ecr.aws/aws-containers/retail-store-sample-assets)   | Serves static assets like images related to the product catalog             |

## Quickstart

The following sections provide quickstart instructions for various platforms. All of these assume that you have cloned this repository locally and are using a CLI thats current directory is the root of the code repository.

### Kubernetes

This deployment method will run the application in an existing Kubernetes cluster.

Pre-requisites:
- Kubernetes cluster
- `kubectl` installed locally

Use `kubectl` to run the application:

```
kubectl apply -f https://raw.githubusercontent.com/aws-containers/retail-store-sample-app/main/dist/kubernetes/deploy.yaml
kubectl wait --for=condition=available deployments --all
```

Get the URL for the frontend load balancer like so:

```
kubectl get svc ui
```

To remove the application use `kubectl` again:

```
kubectl delete -f https://raw.githubusercontent.com/aws-containers/retail-store-sample-app/main/dist/kubernetes/deploy.yaml
```

### Docker Compose

This deployment method will run the application on your local machine using `docker-compose`, and will build the containers as part of the deployment.

Pre-requisites:
- Docker installed locally

Change directory to the Docker Compose deploy directory:

```
cd dist/docker-compose
```

Use `docker compose` to run the application containers:

```
MYSQL_PASSWORD='<some password>' docker compose --file dist/docker-compose/docker-compose.yml up
```

Open the frontend in a browser window:

```
http://localhost:8888
```

To stop the containers in `docker compose` use Ctrl+C. To delete all the containers and related resources run:

```
docker compose -f dist/docker-compose/docker-compose.yml down
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the MIT-0 License.

This package depends on and may incorporate or retrieve a number of third-party
software packages (such as open source packages) at install-time or build-time
or run-time ("External Dependencies"). The External Dependencies are subject to
license terms that you must accept in order to use this package. If you do not
accept all of the applicable license terms, you should not use this package. We
recommend that you consult your company’s open source approval policy before
proceeding.

Provided below is a list of External Dependencies and the applicable license
identification as indicated by the documentation associated with the External
Dependencies as of Amazon's most recent review.

THIS INFORMATION IS PROVIDED FOR CONVENIENCE ONLY. AMAZON DOES NOT PROMISE THAT
THE LIST OR THE APPLICABLE TERMS AND CONDITIONS ARE COMPLETE, ACCURATE, OR
UP-TO-DATE, AND AMAZON WILL HAVE NO LIABILITY FOR ANY INACCURACIES. YOU SHOULD
CONSULT THE DOWNLOAD SITES FOR THE EXTERNAL DEPENDENCIES FOR THE MOST COMPLETE
AND UP-TO-DATE LICENSING INFORMATION.

YOUR USE OF THE EXTERNAL DEPENDENCIES IS AT YOUR SOLE RISK. IN NO EVENT WILL
AMAZON BE LIABLE FOR ANY DAMAGES, INCLUDING WITHOUT LIMITATION ANY DIRECT,
INDIRECT, CONSEQUENTIAL, SPECIAL, INCIDENTAL, OR PUNITIVE DAMAGES (INCLUDING
FOR ANY LOSS OF GOODWILL, BUSINESS INTERRUPTION, LOST PROFITS OR DATA, OR
COMPUTER FAILURE OR MALFUNCTION) ARISING FROM OR RELATING TO THE EXTERNAL
DEPENDENCIES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, EVEN
IF AMAZON HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. THESE LIMITATIONS
AND DISCLAIMERS APPLY EXCEPT TO THE EXTENT PROHIBITED BY APPLICABLE LAW.

MySQL Community Edition - [LICENSE](https://github.com/mysql/mysql-server/blob/5.7/LICENSE)
