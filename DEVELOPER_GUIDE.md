# Developer Guide

This document provides details regarding how to make changes to the repository.

## Getting started

Clone the repository then run `yarn install` in the root.

## Structure

The repository structure is as follows:

TODO

## Using devenv

Configuration files for [`devenv`](https://devenv.sh/) are provided. If you install it you can set up a development environment with all dependencies like so:

```
devenv shell
```

The alternative is to install all the dependencies manually.

## Using nx

The [nx](https://nx.dev/) build system is used to provide a consistent experience across the various projects in the mono repo.

You can run `nx` using `yarn nx`. Common tasks include:

- `yarn nx build`: Builds a component
- `yarn nx test`: Runs unit tests
- `yarn nx test:integration`: Runs integration tests
- `yarn nx serve`: Runs a component so it can be accessed locally on port 8080
- `yarn nx container`: Builds a container image

You can run these for a single component:

```
yarn nx test ui
```

Or for a group, for example this would run unit tests for all application services:

```
yarn nx run-many -t test --projects=tag:service
```

The `service` tag is applied to all application components.

## Building container images

You can build container images for a single component:

```
yarn nx container ui
```

Or for all the components in parallel:

```
yarn nx run-many -t container --projects=tag:service
```

You can customize the image tags like so:

```
yarn nx run-many -t container --projects=tag:service --tags 1234567890.dkr.ecr.us-west-2.amazonaws.com/aws-containers/retail-store-sample-{projectName}:example
```

### Publishing images

You can publish the images by using the `publish` configuration and specifying an appropriate tag. For example to publish to an Amazon ECR private repository it would look something like this:

```
yarn nx container ui --configuration publish --tags 1234567890.dkr.ecr.us-west-2.amazonaws.com/aws-containers/retail-store-sample-{projectName}:example
```

Note: For ECR you must have created the repositories for the images you are trying to publish.

## Docker compose

Each component has its own Docker Compose file along side its source code. For example `src/ui/docker-compose.yml`. These are set up to build the image locally before running the compose file.

You can run a single component with its dependencies like so:

```
yarn nx compose:up catalog
```

If you want to run all the application components from your local repository you can run this command:

```
yarn nx compose-app:up
```

Then this to tear it down:

```
yarn nx compose-app:down
```
