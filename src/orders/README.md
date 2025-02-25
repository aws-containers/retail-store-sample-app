# AWS Containers Retail Sample - Orders Service

| Language | Persistence |
| -------- | ----------- |
| Java     | MySQL       |

This service provides an API for storing orders. Data is stored in MySQL.

## Configuration

The following environment variables are available for configuring the service:

| Name                                          | Description                                                                             | Default     |
| --------------------------------------------- | --------------------------------------------------------------------------------------- | ----------- |
| `PORT`                                        | The port which the server will listen on                                                | `8080`      |
| `RETAIL_CHECKOUT_PERSISTENCE_PROVIDER`        | The persistence provider to use, can be `in-memory` or `postgres`.                      | `in-memory` |
| `RETAIL_ORDERS_PERSISTENCE_POSTGRES_ENDPOINT` | The postgres database endpoint.                                                         | `""`        |
| `RETAIL_ORDERS_PERSISTENCE_POSTGRES_NAME`     | The postgres database name                                                              | `""`        |
| `RETAIL_ORDERS_PERSISTENCE_POSTGRES_USERNAME` | Username to authenticate with postgres database.                                        | `""`        |
| `RETAIL_ORDERS_PERSISTENCE_POSTGRES_PASSWORD` | Password to authenticate with postgres database.                                        | `in-memory` |
| `RETAIL_ORDERS_MESSAGING_PROVIDER`            | The messaging provider to use to publish events. Can be `in-memory`, `sqs`, `rabbitmq`. | `""`        |
| `RETAIL_ORDERS_MESSAGING_SQS_TOPIC`           | The name of the SQS topic to publish events to (SQS messaging provider)                 | `""`        |
| `RETAIL_ORDERS_MESSAGING_RABBITMQ_ADDRESSES`  | Endpoints for RabbitMQ messaing provider                                                | `""`        |

## Running

There are two main options for running the service:

### Local

Pre-requisites:

- Java 21 installed

Run the Spring Boot application like so:

```
./mvnw spring-boot:run
```

Test access:

```
curl localhost:8080/orders
```

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
