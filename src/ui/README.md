# AWS Containers Retail Sample - UI Service

| Language | Persistence |
|---|---|
| Java | N/A |

This service provides the frontend for the retail store, serving the HTML UI and aggregating calls to the backend API components.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| `PORT` | The port which the server will listen on | `8080` |
| `ENDPOINTS_CATALOG` | The endpoint of the catalog API. If set to `false` uses a mock implementation | `false` |
| `ENDPOINTS_CARTS` | The endpoint of the carts API. If set to `false` uses a mock implementation | `false` |
| `ENDPOINTS_ORDERS` | The endpoint of the orders API. If set to `false` uses a mock implementation | `false` |
| `ENDPOINTS_CHECKOUT` | The endpoint of the checkout API. If set to `false` uses a mock implementation | `false` |
| `ENDPOINTS_ASSETS` | The endpoint of the assets service. If set to `false` uses a mock implementation | `false` |
| `ENDPOINTS_HTTP_KEEPALIVE` | Set to false to disable HTTP keepalive on requests to backend services | `true` |
| `RETAIL_UI_BANNER` | Sets text for a banner which will be displayed at the top of the UI on every page | `""` |

## Running

There are two main options for running the service:

### Local

Pre-requisites:
- Java 17 installed

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
