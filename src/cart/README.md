# Cart Service

This API service provides shopping carts for users with the following characteristics:

- Spring Boot application framework
- Persistence using in-memory, AWS DynamoDB or MongoDB
- Ability to introduce turbulent conditions with embedded "Chaos Monkey"

## Running

### Java

The service can be run locally using the following command:

```
./mvnw spring-boot:run
```

This will use an in-memory configuration to persist the shopping carts.

### Docker Compose

The service can be run with Docker Compose using the following command:

```
docker-compose up
```

This will use the `amazon/dynamodb-local` image to provide a local DynamoDB instance to persist the shopping carts.

## Configuration

TODO: Environment variables / profiles

## OpenAPI Spec

TODO: Link to spec

## Chaos Experiments

This service uses the `chaos-monkey-spring-boot` project to allow configuration of turblent conditions at the application level. This can be used to simulate deployments that introduce behavior or performance regressions, and its helpful to test blue/green or canary style deployments that should fail and/or rollback under certain conditions.

For example, to activate latency problems run the application like so:

```
SPRING_PROFILES_ACTIVE=chaos-monkey,chaos-latency ./mvw spring-boot:run
```

The `chaos-monkey` profile is required to activate `chaos-monkey-spring-boot`, at the following profiles provided can set up specific conditions:
- `chaos-latency` adds between 1000 to 3000 milliseconds of latency to every request
- `chaos-errors` causes all `RestController` methods to return HTTP 500 errors