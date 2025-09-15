# AWS Containers Retail Sample - Orders Service

| Language | Persistence |
| -------- | ----------- |
| Java     | PostgreSQL  |

This service provides an API for storing orders. Data is stored in PostgreSQL.

## Configuration

The following environment variables are available for configuring the service:

| Name                                          | Description                                                                             | Default       |
| --------------------------------------------- | --------------------------------------------------------------------------------------- | ------------- |
| `PORT`                                        | The port which the server will listen on                                                | `8080`        |
| `RETAIL_CHECKOUT_PERSISTENCE_PROVIDER`        | The persistence provider to use, can be `in-memory` or `postgres`.                      | `in-memory`   |
| `RETAIL_ORDERS_PERSISTENCE_ENDPOINT`          | The postgres database endpoint.                                                         | `""`          |
| `RETAIL_ORDERS_PERSISTENCE_NAME`              | The postgres database name                                                              | `""`          |
| `RETAIL_ORDERS_PERSISTENCE_USERNAME`          | Username to authenticate with postgres database.                                        | `""`          |
| `RETAIL_ORDERS_PERSISTENCE_PASSWORD`          | Password to authenticate with postgres database.                                        | `""`          |
| `RETAIL_ORDERS_MESSAGING_PROVIDER`            | The messaging provider to use to publish events. Can be `in-memory`, `sqs`, `rabbitmq`. | `"in-memory"` |
| `RETAIL_ORDERS_MESSAGING_SQS_TOPIC`           | The name of the SQS topic to publish events to (SQS messaging provider)                 | `""`          |
| `RETAIL_ORDERS_MESSAGING_RABBITMQ_ADDRESSES`  | Endpoints for RabbitMQ messaging provider, format `host:port`                           | `""`          |
| `RETAIL_ORDERS_MESSAGING_RABBITMQ_USERNAME`   | Username for RabbitMQ messaging provider                                                | `""`          |
| `RETAIL_ORDERS_MESSAGING_RABBITMQ_PASSWORD`   | Password for RabbitMQ messaging provider                                                | `""`          |

## Endpoints

Several "utility" endpoints are provided with useful functionality for various scenarios:

| Method   | Name                     | Description                                                                        |
| -------- | ------------------------ | ---------------------------------------------------------------------------------- |
| `POST`   | `/chaos/status/{code}`   | All HTTP requests to API paths will return the given HTTP status code              |
| `DELETE` | `/chaos/status`          | Disables the HTTP status response above                                            |
| `POST`   | `/chaos/latency/{delay}` | All HTTP requests to API paths will have the specified delay added in milliseconds |
| `DELETE` | `/chaos/latency`         | Disables the HTTP response latency above                                           |
| `POST`   | `/chaos/health`          | Causes all health check requests to fail                                           |
| `DELETE` | `/chaos/health`          | Returns the health check to its default behavior                                   |

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
