# AWS Containers Retail Sample - Catalog Service

| Language | Persistence |
|---|---|
| Go | MySQL |

This service provides an API for retrieving product catalog information. Data is stored in a MySQL database.

## Configuration

The following environment variables are available for configuring the service:

| Name | Description | Default |
|---|---|---|
| PORT | The port which the server will listen on | `8080` |
| DB_ENDPOINT | The endpoint of the MySQL database | `catalog-db:3306` |
| DB_READ_ENDPOINT | (Optional) The read endpoint of the MySQL database |  |
| DB_NAME | The name of the database to connect to | `sampledb` |
| DB_USER | The username for authenticating to the database | `catalog_user` |
| DB_PASSWORD | The password for authenticating to the database | |
| DB_MIGRATE | Whether to run the database migration function on startup | `true` |
| DB_CONNECT_TIMEOUT | Database connection timeout in seconds | `5` |

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
MYSQL_PASSWORD="testing" docker compose up
```

Test access:

```
curl localhost:8080/catalogue
```

To clean up:

```
docker compose down
```