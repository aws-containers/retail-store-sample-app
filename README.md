# AWS Containers Retail Sample

This is a sample application designed to illustrate various concepts related to containers on AWS. It presents a sample retail store application including a product catalog, shopping cart and checkout.

## Application Architecture

The application has been deliberately over-engineered to generate multiple de-coupled components. These components generally have different infrastructure dependencies, and may support multiple "backends" (example: Carts service supports MongoDB or DynamoDB).

![Architecture](/docs/images/architecture.png)

| Component | Language | Dependencies        | Description                                                                 |
|-----------|----------|---------------------|-----------------------------------------------------------------------------|
| UI        | Java     | None                | Aggregates API calls to the various other services and renders the HTML UI. |
| Catalog   | Go       | MySQL               | Product catalog API                                                         |
| Carts     | Java     | DynamoDB or MongoDB | User shopping carts API                                                     |
| Orders    | Java     | MySQL               | User orders API                                                             |
| Checkout  | Node     | Redis               | API to orchestrate the checkout process                                     |
| Assets    | Nginx    |                     | Serves static assets like images related to the product catalog             |

## Quickstart

The following sections provide quickstart instructions for various platforms. All of these assume that you have cloned this repository locally and are using a CLI thats current directory is the root of the code repository.

### Docker Compose

This deployment method will run the application on your local machine using `docker-compose`, and will build the containers as part of the deployment.

Pre-requisites:
- Docker and Docker Compose installed locally

Change directory to the Docker Compose deploy directory:

```
cd deploy/docker-compose
```

Use `docker-compose` to run the application containers:

```
docker-compose up
```

Open the frontend in a browser window:

```
http://localhost
```