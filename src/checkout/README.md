# AWS Containers Retail Sample - Checkout Service

| Language | Persistence |
| -------- | ----------- |
| Node     | Redis       |

This service provides an API for storing customer data during the checkout process. Data is stored in Redis.

## Configuration

The following environment variables are available for configuring the service:

| Name                                    | Description                                                              | Default     |
| --------------------------------------- | ------------------------------------------------------------------------ | ----------- |
| `PORT`                                  | The port which the server will listen on                                 | `8080`      |
| `RETAIL_CHECKOUT_PERSISTENCE_PROVIDER`  | The persistence provider to use, can be `in-memory` or `redis`.          | `in-memory` |
| `RETAIL_CHECKOUT_PERSISTENCE_REDIS_URL` | The endpoint of the Redis server used to store state.                    | `""`        |
| `RETAIL_CHECKOUT_ENDPOINTS_ORDERS`      | The endpoint of the orders API. If empty uses a mock implementation      | `""`        |
| `RETAIL_CHECKOUT_SHIPPING_NAME_PREFIX`  | A string prefix that can be applied to the names of the shipping options | `""`        |

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
