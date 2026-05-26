# AWS Containers Retail Sample - Recommendations Service

This directory contains the OpenAPI specification for the Recommendations service, which serves product recommendations for the retail store sample application.

The recommendations service itself is **not implemented**. The UI service consumes this OpenAPI spec to generate a typed clientand ships with a built-in stub implementation that returns randomly chosen products from the catalog. This makes it possible to slot in a real backend without touching the UI's calling code.

## Endpoint

| Method | Path                           | Description                                |
| ------ | ------------------------------ | ------------------------------------------ |
| `GET`  | `/recommendations/{productId}` | Returns recommended products for a product |

See [openapi.yml](openapi.yml) for the full schema.
