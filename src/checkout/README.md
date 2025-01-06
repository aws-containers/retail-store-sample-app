# AWS Containers Retail Sample - Checkout Service

| Language | Persistence |
|---|---|
| Node | Redis |

This service provides an API for storing customer data during the checkout process. Data is stored in Redis.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| `PORT` | The port which the server will listen on | `8080` |
| `ENDPOINTS_ORDERS` | The endpoint of the orders API. If empty uses a mock implementation | `""` |
| `REDIS_URL` | The endpoint of the Redis server used to write data. If empty use in-memory storage | `""` |
| `REDIS_READER_URL` | The endpoint of the Redis server used to read data. If empty use the value of `REDIS_URL` | `""` |
| `SHIPPING_NAME_PREFIX` | A string prefix that can be applied to the names of the shipping options | `""` |

## Running

There are two main options for running the service:

### Local

Pre-requisites:
- Node.JS >= 16 installed

Run the application like so:

```
yarn start
```

The API endpoint will be available at `http://localhost:8080`.

### Docker

A `docker-compose.yml` file is included to run the service in Docker:

```
docker compose up
```

The API endpoint will be available at `http://localhost:8080`.

To clean up:

```
docker compose down
```
