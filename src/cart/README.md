# AWS Containers Retail Sample - Cart Service

| Language | Persistence     |
| -------- | --------------- |
| Java     | Amazon DynamoDB |

This service provides an API for storing customer shopping carts. Data is stored in Amazon DynamoDB.

## Configuration

The following environment variables are available for configuring the service:

| Name                                           | Description                                                        | Default     |
| ---------------------------------------------- | ------------------------------------------------------------------ | ----------- |
| `PORT`                                         | The port which the server will listen on                           | `8080`      |
| `RETAIL_CART_PERSISTENCE_PROVIDER`             | The persistence provider to use, can be `in-memory` or `dynamodb`. | `in-memory` |
| `RETAIL_CART_PERSISTENCE_DYNAMODB_TABLENAME`   | The name of the Amazon DynamoDB table used for persistence         | `Items`     |
| `RETAIL_CART_PERSISTENCE_DYNAMODB_ENDPOINT`    | The Amazon DynamoDB endpoint to use                                | `""`        |
| `RETAIL_CART_PERSISTENCE_DYNAMODB_CREATETABLE` | Enable to automatically create the Amazon DynamoDB table required  | `false`     |

## Running

There are two main options for running the service:

### Local

Pre-requisites:

- Java 21 installed

Run the Spring Boot application like so:

```
./mvnw spring-boot:run
```

Test the application by visiting `http://localhost:8080` in a web browser.

### Docker

A `docker-compose.yml` file is included to run the service in Docker:

```
docker compose up
```

Test the application by visiting `http://localhost:8080` in a web browser.

To clean up:

```
docker compose down
```
