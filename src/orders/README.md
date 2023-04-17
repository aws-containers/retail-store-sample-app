# AWS Containers Retail Sample - Orders Service

| Language | Persistence |
|---|---|
| Java | MySQL |

This service provides an API for storing orders. Data is stored in MySQL.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| `PORT` | The port which the server will listen on | `8080` |
| `SPRING_DATASOURCE_WRITER_URL` | The URL for the MySQL database writer endpoint. Uses the format `jdbc:mariadb://<host>:<port>/<database>` | `""` |
| `SPRING_DATASOURCE_WRITER_USERNAME` | The username for the MySQL database writer endpoint. | `""` |
| `SPRING_DATASOURCE_WRITER_PASSWORD` | The password for the MySQL database writer endpoint. | `""` |
| `SPRING_DATASOURCE_READER_URL` | The URL for the MySQL database reader endpoint. Uses the format `jdbc:mariadb://<host>:<port>/<database>` | `""` |
| `SPRING_DATASOURCE_READER_USERNAME` | The username for the MySQL database reader endpoint. | `""` |
| `SPRING_DATASOURCE_READER_PASSWORD` | The password for the MySQL database reader endpoint. | `""` |
| `SPRING_RABBITMQ_ADDRESSES` | The address of the RabbitMQ endpoints. Uses the format `amqp://<endpoint>:<port>` | `""` |

## Running

There are two main options for running the service:

### Local

Pre-requisites:
- Java 17 installed

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