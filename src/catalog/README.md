# AWS Containers Retail Sample - Catalog Service

| Language | Persistence | Search     |
| -------- | ----------- | ---------- |
| Go       | MySQL       | OpenSearch |

This service provides an API for retrieving product catalog information. Data is stored in a MySQL database.

## Configuration

The following environment variables are available for configuring the service:

| Name                                       | Description                                                     | Default                 |
| ------------------------------------------ | --------------------------------------------------------------- | ----------------------- |
| PORT                                       | The port which the server will listen on                        | `8080`                  |
| RETAIL_CATALOG_PERSISTENCE_PROVIDER        | The persistence provider to use, can be `in-memory` or `mysql`. | `in-memory`             |
| RETAIL_CATALOG_PERSISTENCE_ENDPOINT        | Database endpoint URL                                           | `""`                    |
| RETAIL_CATALOG_PERSISTENCE_DB_NAME         | Database name                                                   | `catalogdb`             |
| RETAIL_CATALOG_PERSISTENCE_USER            | Database user                                                   | `catalog_user`          |
| RETAIL_CATALOG_PERSISTENCE_PASSWORD        | Database password                                               | `""`                    |
| RETAIL_CATALOG_PERSISTENCE_CONNECT_TIMEOUT | Database connection timeout in seconds                          | `5`                     |
| RETAIL_CATALOG_SEARCH_ENABLED              | Enable or disable search                                        | `false`                 |
| RETAIL_CATALOG_SEARCH_OS_ENDPOINT          | OpenSearch endpoint URL                                         | `http://localhost:9200` |
| RETAIL_CATALOG_SEARCH_OS_INDEX             | Index name                                                      | `products`              |
| RETAIL_CATALOG_SEARCH_OS_USERNAME          | OpenSearch user                                                 | `admin`                 |
| RETAIL_CATALOG_SEARCH_OS_PASSWORD          | OpenSearch password                                             | `""`                    |
| RETAIL_CATALOG_SEARCH_OS_TLS_SKIP_VERIFY   | Skip TLS certificate verification for OpenSearch                | `false`                 |
| RETAIL_CATALOG_SEARCH_PROVIDER             | Search provider (aws or self-hosted)                            | `self-hosted`           |

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
