# AWS Containers Retail Sample - Cart Service

| Language | Persistence |
|---|---|
| Java | Amazon DynamoDB |

This service provides an API for storing customer shopping carts. Data is stored in Amazon DynamoDB.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| `CARTS_DYNAMODB_ENDPOINT` | The Amazon DynamoDB endpoint to use | ` ` |
| `CARTS_DYNAMODB_CREATETABLE` | Enable to automatically create the Amazon DynamoDB table required | `false` |

## Running

You can run the application locally from the directory that has the project file (cart.csproj)

### Local

Pre-requisites:
- DotNet 7.0 cli

Use the DotNet cli:

```
dotnet run
```

Test the application by visiting `http://localhost:5155/swagger/index.html` in a web browser.