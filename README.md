# Zephyr Archaeotech Emporium Online Store

This is the source repository for the online store of the Zephyr Archaeotech Emporium. It's used in Pulumi's Zephyr series of blog posts to discuss best practices when using Pulumi to manage infrastructure and applications.

This application is based on [this source repository](https://github.com/aws-containers/retail-store-sample-app); the original `README.md` for the source is now found in the `/docs` folder.

## Deploying with Pulumi

Information on using Pulumi to deploy this application and its associated infrastructure can be found in the `infra` folder.

## Deploying to Docker Compose

This application can be deployed locally via Docker Compose; use the `docker-compose.yml` file in the `deploy/docker-compose` folder.

## Deploying to Kubernetes

This application can be deployed to Kubernetes directly; use the `deploy.yaml` file in the `deploy/kubernetes` folder.
