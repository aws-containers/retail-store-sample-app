# AWS Containers Retail Sample - Catalog Service

| Language | Persistence |
| -------- | ----------- |
| Go       | MySQL       |

This service provides an API for retrieving product catalog information. Data is stored in a MySQL database.

## Configuration

The following environment variables are available for configuring the service:

| Name                                       | Description                                                     | Default        |
| ------------------------------------------ | --------------------------------------------------------------- | -------------- |
| PORT                                       | The port which the server will listen on                        | `8080`         |
| RETAIL_CATALOG_PERSISTENCE_PROVIDER        | The persistence provider to use, can be `in-memory` or `mysql`. | `in-memory`    |
| RETAIL_CATALOG_PERSISTENCE_ENDPOINT        | Database endpoint URL                                           | `""`           |
| RETAIL_CATALOG_PERSISTENCE_DB_NAME         | Database name                                                   | `catalogdb`    |
| RETAIL_CATALOG_PERSISTENCE_USER            | Database user                                                   | `catalog_user` |
| RETAIL_CATALOG_PERSISTENCE_PASSWORD        | Database password                                               | `""`           |
| RETAIL_CATALOG_PERSISTENCE_CONNECT_TIMEOUT | Database connection timeout in seconds                          | `5`            |

## Running

There are two main options for running the service:

### Local

Note: You must already have a MySQL database running

Build the binary as follows:

```
go build -o main main.go
```

Then run it:

```
./main
```

Test access:

```
curl localhost:8080/catalogue
```

### Docker

A `docker-compose.yml` file is included to run the service in Docker, including a MySQL database:

```
DB_PASSWORD="testing" docker compose up
```

Test access:

```
curl localhost:8080/catalogue
```

To clean up:

```
docker compose down
```
