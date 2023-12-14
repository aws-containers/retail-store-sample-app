# AWS Containers Retail Sample - Cart Service

| Language | Persistence |
|---|---|
| Java | Amazon DynamoDB |

This service provides an API for storing customer shopping carts. Data is stored in Amazon DynamoDB.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| `PORT` | The port which the server will listen on | `8080` |
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

The above command will run the cart API on port 8080. You can change the port with either of the below approaches.

Set the PORT environment variable and then run the dotnet cli, for example on a Mac this can be accomplished with these commands:

```
export PORT=5155
dotnet run
```

Or if you want to dynamically change the port with the dotnet run cli directly, you can pass in a custom url:

```
dotnet run --urls "http://localhost:5155"
```

Test the application by visiting `http://localhost:5155/swagger/index.html` in a web browser.