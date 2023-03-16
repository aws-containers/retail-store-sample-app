import * as pulumi from "@pulumi/pulumi";
import * as docker from "@pulumi/docker";

// Get configuration values
const config = new pulumi.Config();
const srcRepoPath = config.require("srcRepoPath");
const mySqlPassword = config.requireSecret("mySqlPassword");

// Build the UI image
const uiImage = new docker.Image("ui-image", {
    build: {
        args: {
            "JAR_PATH": "target/ui-0.0.1-SNAPSHOT.jar",
        },
        context: `${srcRepoPath}/src/ui`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-ui:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build the assets image
const assetsImage = new docker.Image("assets-image", {
    build: {
        context: `${srcRepoPath}/src/assets`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-assets:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build carts image
const cartsImage = new docker.Image("carts-image", {
    build: {
        args: {
            "JAR_PATH": "target/carts-0.0.1-SNAPSHOT.jar",
        },
        context: `${srcRepoPath}/src/cart`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-carts:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build catalog image
const catalogImage = new docker.Image("catalog-image", {
    build: {
        args: {
            "MAIN_PATH": "main.go",
        },
        context: `${srcRepoPath}/src/catalog`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-catalog:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build checkout image
const checkoutImage = new docker.Image("checkout-image", {
    build: {
        args: {
            "MAIN_PATH": "main.go",
        },
        context: `${srcRepoPath}/src/checkout`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-checkout:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build orders image
const ordersImage = new docker.Image("orders-image", {
    build: {
        args: {
            "JAR_PATH": "target/orders-0.0.1-SNAPSHOT.jar",
        },
        context: `${srcRepoPath}/src/orders`,
        dockerfile: "Dockerfile",
        platform: "linux/arm64",
    },
    imageName: "slowe/zephyr-orders:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Pull other images needed by the application
// First MariaDB, used by catalog-db and orders-db
const mariaDbRegistryImage = docker.getRegistryImage({
    name: "mariadb:10.9",
});
const mariaDbRemoteImage = new docker.RemoteImage("mariadb-remote-image", {
    name: mariaDbRegistryImage.then(mariaDbRegistryImage => mariaDbRegistryImage.name),
    pullTriggers: [mariaDbRegistryImage.then(mariaDbRegistryImage => mariaDbRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Next DynamoDB, used by carts-db
const dynamoDbRegistryImage = docker.getRegistryImage({
    name: "amazon/dynamodb-local:1.20.0",
});
const dynamoDbRemoteImage = new docker.RemoteImage("dynamodb-remote-image", {
    name: dynamoDbRegistryImage.then(dynamoDbRegistryImage => dynamoDbRegistryImage.name),
    pullTriggers: [dynamoDbRegistryImage.then(dynamoDbRegistryImage => dynamoDbRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Third RabbitMQ, used by rabbitmq
const rabbitMqRegistryImage = docker.getRegistryImage({
    name: "rabbitmq:3-management",
});
const rabbitMqRemoteImage = new docker.RemoteImage("rabbitmq-remote-image", {
    name: rabbitMqRegistryImage.then(rabbitMqRegistryImage => rabbitMqRegistryImage.name),
    pullTriggers: [rabbitMqRegistryImage.then(rabbitMqRegistryImage => rabbitMqRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Finally Redis, used by checkout-redis
const redisRegistryImage = docker.getRegistryImage({
    name: "redis:6-alpine",
});
const redisRemoteImage = new docker.RemoteImage("redis-remote-image", {
    name: redisRegistryImage.then(redisRegistryImage => redisRegistryImage.name),
    pullTriggers: [redisRegistryImage.then(redisRegistryImage => redisRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Create a network
const network = new docker.Network("network", {
    name: "zephyr-net",
});

// Create an assets container
const assetsContainer = new docker.Container("assets-container", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "PORT=8080",
    ],
    hostname: "assets",
    image: assetsImage.id,
    memory: 64,
    name: "assets",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 8080,
    //         external: 8080,
    //     },
    // ],
    restart: "always",
});

// Create a RabbitMQ container
const rabbitMqContainer = new docker.Container("rabbitmq", {
    image: rabbitMqRemoteImage.repoDigest,
    name: "rabbitmq",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    ports: [
        {
            internal: 5672,
            external: 5672,
        },
        {
            internal: 15672,
            external: 15672,
        },
    ],
});

// Create a Redis container for checkout-redis
const checkoutRedisContainer = new docker.Container("checkout-redis", {
    hostname: "checkout-redis",
    image: redisRemoteImage.repoDigest,
    memory: 128,
    name: "checkout-redis",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    ports: [
        {
            internal: 6379,
            external: 6379,
        },
    ],
    restart: "always",
});

// Create a DynamoDB container for carts-db
const cartsDbContainer = new docker.Container("carts-db", {
    hostname: "carts-db",
    image: dynamoDbRemoteImage.repoDigest,
    memory: 128,
    name: "carts-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    ports: [
        {
            internal: 8000,
            external: 8000,
        },
    ],
    restart: "always",
});

// Launch a MariaDB container for orders-db
const ordersDbContainer = new docker.Container("orders-db", {
    envs: [
        `MYSQL_ROOT_PASSWORD=${mySqlPassword}`,
        "MYSQL_ALLOW_EMPTY_PASSWORD=true",
        "MYSQL_DATABASE=orders",
        "MYSQL_USER=orders_user",
        `MYSQL_PASSWORD=${mySqlPassword}`,
    ],
    hostname: "orders-db",
    image: mariaDbRemoteImage.repoDigest,
    memory: 128,
    name: "orders-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 3306,
    //         external: 3307,
    //     },
    // ],
    restart: "always",
});

// Launch a MariaDB container for catalog-db
const catalogDbContainer = new docker.Container("catalog-db", {
    envs: [
        `MYSQL_ROOT_PASSWORD=${mySqlPassword}`,
        "MYSQL_ALLOW_EMPTY_PASSWORD=true",
        "MYSQL_DATABASE=sampledb",
        "MYSQL_USER=catalog_user",
        `MYSQL_PASSWORD=${mySqlPassword}`,
    ],
    hostname: "catalog-db",
    image: mariaDbRemoteImage.repoDigest,
    memory: 128,
    name: "catalog-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 3306,
    //         external: 3306,
    //     },
    // ],
    restart: "always",
});

// Launch a container for the catalog service
const catalogContainer = new docker.Container("catalog", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "GIN_MODE=release",
        `DB_PASSWORD=${mySqlPassword}`,
    ],
    hostname: "catalog",
    image: catalogImage.id,
    memory: 128,
    name: "catalog",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 8080,
    //         external: 8081,
    //     },
    // ],
    restart: "always",
}, { dependsOn: catalogDbContainer });

// Launch a container for the carts service
const cartsContainer = new docker.Container("carts", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom",
        "SERVER_TOMCAT_ACCESSLOG_ENABLED=true",
        "SPRING_PROFILES_ACTIVE=dynamodb",
        "CARTS_DYNAMODB_ENDPOINT=http://carts-db:8000",
        "CARTS_DYNAMODB_CREATETABLE=true",
        "AWS_ACCESS_KEY_ID=key",
        "AWS_SECRET_ACCESS_KEY=dummy",
    ],
    hostname: "carts",
    image: cartsImage.id,
    memory: 256,
    name: "carts",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 8080,
    //         external: 8080,
    //     },
    // ],
    restart: "always",
}, { dependsOn: cartsDbContainer });

//Launch a container for the orders service
const ordersContainer = new docker.Container("orders", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom",
        "SERVER_TOMCAT_ACCESSLOG_ENABLED=true",
        "SPRING_PROFILES_ACTIVE=mysql,rabbitmq",
        "SPRING_DATASOURCE_WRITER_URL=jdbc:mariadb://orders-db:3306/orders",
        "SPRING_DATASOURCE_WRITER_USERNAME=orders_user",
        `SPRING_DATASOURCE_WRITER_PASSWORD=${mySqlPassword}`,
        "SPRING_DATASOURCE_READER_URL=jdbc:mariadb://orders-db:3306/orders",
        "SPRING_DATASOURCE_READER_USERNAME=orders_user",
        `SPRING_DATASOURCE_READER_PASSWORD=${mySqlPassword}`,
        "SPRING_RABBITMQ_HOST=rabbitmq",
    ],
    hostname: "orders",
    image: ordersImage.id,
    memory: 256,
    name: "orders",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 8080,
    //         external: 8083,
    //     },
    // ],
    restart: "always",
}, { dependsOn: ordersDbContainer });

//Launch a container for the checkout service
const checkoutContainer = new docker.Container("checkout", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "REDIS_URL=redis://checkout-redis:6379",
        "ENDPOINTS_ORDERS=http://orders:8080",
      ],
    hostname: "checkout",
    image: checkoutImage.id,
    memory: 256,
    name: "checkout",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    // ports: [
    //     {
    //         internal: 8080,
    //         external: 8084,
    //     },
    // ],
    readOnly: true,
    restart: "always",
    tmpfs: {
        "/tmp": "rw,noexec,nosuid",
    },
}, { dependsOn: checkoutRedisContainer });

// Launch a UI container
const uiContainer = new docker.Container("ui", {
    capabilities: {
        drops: ["ALL"],
    },
    envs: [
        "JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/urandom",
        "SERVER_TOMCAT_ACCESSLOG_ENABLED=true",
        "ENDPOINTS_CATALOG=http://catalog:8080",
        "ENDPOINTS_CARTS=http://carts:8080",
        "ENDPOINTS_ORDERS=http://orders:8080",
        "ENDPOINTS_CHECKOUT=http://checkout:8080",
        "ENDPOINTS_ASSETS=http://assets:8080",
    ],
    hostname: "ui",
    image: uiImage.id,
    memory: 256,
    name: "ui",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    ports: [
        {
            internal: 8080,
            external: 8888,
        },
    ],
    restart: "always",
});
