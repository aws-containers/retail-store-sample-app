# AWS Containers Retail Sample - UI Service

| Language | Persistence |
| -------- | ----------- |
| Java     | N/A         |

This service provides the frontend for the retail store, serving the HTML UI and aggregating calls to the backend API components.

## Configuration

The following environment variables are available for configuring the service:

| Name                              | Description                                                                                            | Default                 |
| --------------------------------- | ------------------------------------------------------------------------------------------------------ | ----------------------- |
| `PORT`                            | The port which the server will listen on                                                               | `8080`                  |
| `RETAIL_UI_THEME`                 | Name of the theme for the UI, valid values are `default`, `green`, `orange`, `teal`                    | `"default"`             |
| `RETAIL_UI_DISABLE_DEMO_WARNINGS` | Disable the UI messages warning about demonstration content                                            | `false`                 |
| `RETAIL_UI_PRODUCT_IMAGES_PATH`   | Overrides the location where the sample product images are sourced from to use the specified file path | ``                      |
| `RETAIL_UI_ENDPOINTS_CATALOG`     | The endpoint of the catalog API. If set to `false` uses a mock implementation                          | `false`                 |
| `RETAIL_UI_ENDPOINTS_CARTS`       | The endpoint of the carts API. If set to `false` uses a mock implementation                            | `false`                 |
| `RETAIL_UI_ENDPOINTS_ORDERS`      | The endpoint of the orders API. If set to `false` uses a mock implementation                           | `false`                 |
| `RETAIL_UI_ENDPOINTS_CHECKOUT`    | The endpoint of the checkout API. If set to `false` uses a mock implementation                         | `false`                 |
| `RETAIL_UI_CHAT_ENABLED`          | Enable the chat bot UI                                                                                 | `false`                 |
| `RETAIL_UI_CHAT_PROVIDER`         | The chat provider to use, value values are `bedrock`, `openai`, `mock`                                 | `""`                    |
| `RETAIL_UI_CHAT_MODEL`            | The chat model to use, depends on the provider.                                                        | `""`                    |
| `RETAIL_UI_CHAT_TEMPERATURE`      | Model temperature                                                                                      | `0.6`                   |
| `RETAIL_UI_CHAT_MAX_TOKENS`       | Model maximum response tokens                                                                          | `300`                   |
| `RETAIL_UI_CHAT_PROMPT`           | Model system prompt                                                                                    | `(see source)`          |
| `RETAIL_UI_CHAT_BEDROCK_REGION`   | Amazon Bedrock region                                                                                  | `""`                    |
| `RETAIL_UI_CHAT_OPENAI_BASE_URL`  | Base URL for OpenAI endpoint                                                                           | `http://localhost:8888` |
| `RETAIL_UI_CHAT_OPENAI_API_KEY`   | API key for OpenAI endpoint                                                                            | `""`                    |

## Endpoints

Several "utility" endpoints are provided with useful functionality for various scenarios:

| Method | Name                           | Description                                                                 |
| ------ | ------------------------------ | --------------------------------------------------------------------------- |
| `GET`  | `/utility/status/{code}`       | Returns HTTP response with given HTTP status code                           |
| `GET`  | `/utility/headers`             | Print the HTTP headers of the inbound request                               |
| `GET`  | `/utility/panic`               | Shutdown the application with an error code                                 |
| `POST` | `/utility/echo`                | Write back the POST payload sent                                            |
| `POST` | `/utility/store`               | Write the payload to a file and return a hash                               |
| `GET`  | `/utility/store/{hash}`        | Return the payload from the file system previously written                  |
| `GET`  | `/utility/stress/{iterations}` | Stress the CPU with the number of iterations increasing the CPU consumption |

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
