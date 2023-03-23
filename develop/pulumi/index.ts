import * as pulumi from "@pulumi/pulumi";
import * as docker from "@pulumi/docker";

// Get configuration values
const config = new pulumi.Config();
const srcRepoPath = config.require("srcRepoPath");
const mySqlPassword = config.require("mySqlPassword");
const assetsFlag = config.get("assetsFlag") || "build";
const cartsFlag = config.get("cartsFlag") || "build";
const catalogFlag = config.get("catalogFlag") || "build";
const checkoutFlag = config.get("checkoutFlag") || "build";
const ordersFlag = config.get("ordersFlag") || "build";

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
    imageName: "zephyr-ui:latest",
    skipPush: true,
}, { retainOnDelete: true });

// Build the assets image or pull remote image, depending on value of assetsFlag
var assetsImageRef: pulumi.Input<string>;
if (assetsFlag == "build") {
    const assetsImage = new docker.Image("assets-image", {
        build: {
            context: `${srcRepoPath}/src/assets`,
            dockerfile: "Dockerfile",
            platform: "linux/arm64",
        },
        imageName: "zephyr-assets:latest",
        skipPush: true,
    }, { retainOnDelete: true });
    assetsImageRef = assetsImage.id;
} else {
    const assetsRegistryImage = docker.getRegistryImage({
        name: "public.ecr.aws/aws-containers/retail-store-sample-assets:0.2.0",
    });
    const assetsRemoteImage = new docker.RemoteImage("assets-image", {
        name: assetsRegistryImage.then(assetsRegistryImage => assetsRegistryImage.name),
        pullTriggers: [assetsRegistryImage.then(assetsRegistryImage => assetsRegistryImage.sha256Digest)],
    }, { retainOnDelete: true });
    assetsImageRef = assetsRemoteImage.repoDigest;
};

// Build carts image or pull remote image, depending on the value of cartsFlag
var cartsImageRef: pulumi.Input<string>;
if (cartsFlag == "build") {
    const cartsImage = new docker.Image("carts-image", {
        build: {
            args: {
                "JAR_PATH": "target/carts-0.0.1-SNAPSHOT.jar",
            },
            context: `${srcRepoPath}/src/cart`,
            dockerfile: "Dockerfile",
            platform: "linux/arm64",
        },
        imageName: "zephyr-carts:latest",
        skipPush: true,
    }, { retainOnDelete: true });
    cartsImageRef = cartsImage.id;
} else {
    const cartsRegistryImage = docker.getRegistryImage({
        name: "public.ecr.aws/aws-containers/retail-store-sample-cart:0.2.0",
    });
    const cartsRemoteImage = new docker.RemoteImage("carts-image", {
        name: cartsRegistryImage.then(cartsRegistryImage => cartsRegistryImage.name),
        pullTriggers: [cartsRegistryImage.then(cartsRegistryImage => cartsRegistryImage.sha256Digest)],
    }, { retainOnDelete: true });
    cartsImageRef = cartsRemoteImage.repoDigest;
};

// Build catalog image or pull remote image, depending on the value of catalogFlag
var catalogImageRef: pulumi.Input<string>;
if (catalogFlag == "build") {
    const catalogImage = new docker.Image("catalog-image", {
        build: {
            args: {
                "MAIN_PATH": "main.go",
            },
            context: `${srcRepoPath}/src/catalog`,
            dockerfile: "Dockerfile",
            platform: "linux/arm64",
        },
        imageName: "zephyr-catalog:latest",
        skipPush: true,
    }, { retainOnDelete: true });
    catalogImageRef = catalogImage.id;
} else {
    const catalogRegistryImage = docker.getRegistryImage({
        name: "public.ecr.aws/aws-containers/retail-store-sample-catalog:0.2.0",
    });
    const catalogRemoteImage = new docker.RemoteImage("catalog-image", {
        name: catalogRegistryImage.then(catalogRegistryImage => catalogRegistryImage.name),
        pullTriggers: [catalogRegistryImage.then(catalogRegistryImage => catalogRegistryImage.sha256Digest)],
    }, { retainOnDelete: true });
    catalogImageRef = catalogRemoteImage.repoDigest;   
};

// Build checkout image or pull remote image, depending on value of checkoutFlag
var checkoutImageRef: pulumi.Input<string>;
if (checkoutFlag == "build") {
    const checkoutImage = new docker.Image("checkout-image", {
        build: {
            args: {
                "MAIN_PATH": "main.go",
            },
            context: `${srcRepoPath}/src/checkout`,
            dockerfile: "Dockerfile",
            platform: "linux/arm64",
        },
        imageName: "zephyr-checkout:latest",
        skipPush: true,
    }, { retainOnDelete: true });
    checkoutImageRef = checkoutImage.id;
} else {
    const checkoutRegistryImage = docker.getRegistryImage({
        name: "public.ecr.aws/aws-containers/retail-store-sample-checkout:0.2.0",
    });
    const checkoutRemoteImage = new docker.RemoteImage("checkout-image", {
        name: checkoutRegistryImage.then(checkoutRegistryImage => checkoutRegistryImage.name),
        pullTriggers: [checkoutRegistryImage.then(checkoutRegistryImage => checkoutRegistryImage.sha256Digest)],
    }, { retainOnDelete: true });
    checkoutImageRef = checkoutRemoteImage.repoDigest;
};

// Build orders image or pull remote image, depending on value of ordersFlag
var ordersImageRef: pulumi.Input<string>;
if (ordersFlag == "build") {
    const ordersImage = new docker.Image("orders-image", {
        build: {
            args: {
                "JAR_PATH": "target/orders-0.0.1-SNAPSHOT.jar",
            },
            context: `${srcRepoPath}/src/orders`,
            dockerfile: "Dockerfile",
            platform: "linux/arm64",
        },
        imageName: "zephyr-orders:latest",
        skipPush: true,
    }, { retainOnDelete: true });
    ordersImageRef = ordersImage.id;
} else {
    const ordersRegistryImage = docker.getRegistryImage({
        name: "public.ecr.aws/aws-containers/retail-store-sample-orders:0.2.0",
    });
    const ordersRemoteImage = new docker.RemoteImage("orders-image", {
        name: ordersRegistryImage.then(ordersRegistryImage => ordersRegistryImage.name),
        pullTriggers: [ordersRegistryImage.then(ordersRegistryImage => ordersRegistryImage.sha256Digest)],
    }, { retainOnDelete: true });
    ordersImageRef = ordersRemoteImage.repoDigest;
};

// Pull other images needed by the application
// First MariaDB, used by catalog-db and orders-db
const mariaDbRegistryImage = docker.getRegistryImage({
    name: "mariadb:10.9",
});
const mariaDbImage = new docker.RemoteImage("mariadb-image", {
    name: mariaDbRegistryImage.then(mariaDbRegistryImage => mariaDbRegistryImage.name),
    pullTriggers: [mariaDbRegistryImage.then(mariaDbRegistryImage => mariaDbRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Next DynamoDB, used by carts-db
const dynamoDbRegistryImage = docker.getRegistryImage({
    name: "amazon/dynamodb-local:1.20.0",
});
const dynamoDbImage = new docker.RemoteImage("dynamodb-image", {
    name: dynamoDbRegistryImage.then(dynamoDbRegistryImage => dynamoDbRegistryImage.name),
    pullTriggers: [dynamoDbRegistryImage.then(dynamoDbRegistryImage => dynamoDbRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Third RabbitMQ, used by rabbitmq
const rabbitMqRegistryImage = docker.getRegistryImage({
    name: "rabbitmq:3-management",
});
const rabbitMqImage = new docker.RemoteImage("rabbitmq-image", {
    name: rabbitMqRegistryImage.then(rabbitMqRegistryImage => rabbitMqRegistryImage.name),
    pullTriggers: [rabbitMqRegistryImage.then(rabbitMqRegistryImage => rabbitMqRegistryImage.sha256Digest)],
}, { retainOnDelete: true });

// Finally Redis, used by checkout-redis
const redisRegistryImage = docker.getRegistryImage({
    name: "redis:6-alpine",
});
const redisImage = new docker.RemoteImage("redis-image", {
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
    image: assetsImageRef,
    memory: 64,
    name: "assets",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    restart: "always",
});

// Create a RabbitMQ container
const rabbitMqContainer = new docker.Container("rabbitmq", {
    image: rabbitMqImage.repoDigest,
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
    image: redisImage.repoDigest,
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
    image: dynamoDbImage.repoDigest,
    memory: 128,
    name: "carts-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    restart: "always",
});

// Launch a MariaDB container for orders-db
const ordersDbContainer = new docker.Container("orders-db", {
    envs: [
        `MYSQL_ROOT_PASSWORD=${mySqlPassword}`,
        `MYSQL_PASSWORD=${mySqlPassword}`,
        "MYSQL_ALLOW_EMPTY_PASSWORD=true",
        "MYSQL_DATABASE=orders",
        "MYSQL_USER=orders_user",
    ],
    hostname: "orders-db",
    image: mariaDbImage.repoDigest,
    memory: 128,
    name: "orders-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
    restart: "always",
});

// Launch a MariaDB container for catalog-db
const catalogDbContainer = new docker.Container("catalog-db", {
    envs: [
        `MYSQL_ROOT_PASSWORD=${mySqlPassword}`,
        `MYSQL_PASSWORD=${mySqlPassword}`,
        "MYSQL_ALLOW_EMPTY_PASSWORD=true",
        "MYSQL_DATABASE=sampledb",
        "MYSQL_USER=catalog_user",
    ],
    hostname: "catalog-db",
    image: mariaDbImage.repoDigest,
    memory: 128,
    name: "catalog-db",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
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
    image: catalogImageRef,
    memory: 128,
    name: "catalog",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
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
    image: cartsImageRef,
    memory: 256,
    name: "carts",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
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
    image: ordersImageRef,
    memory: 256,
    name: "orders",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
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
    image: checkoutImageRef,
    memory: 256,
    name: "checkout",
    networksAdvanced: [
        {
            name: network.name,
        },
    ],
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
}, { dependsOn: [ checkoutContainer, catalogContainer, ordersContainer, assetsContainer, cartsContainer ]});
