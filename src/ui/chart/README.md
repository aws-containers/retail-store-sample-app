# UI Helm Chart

This Helm chart deploys the UI component of the application.

## Installation

To install the chart:

```bash
helm install ui oci://public.ecr.aws/aws-containers/retail-store-sample-ui-chart:0.8.5
```

To install with custom values:

```bash
helm install ui oci://public.ecr.aws/aws-containers/retail-store-sample-ui-chart:0.8.5 --values custom-values.yaml
```

## Configuration

The following table lists the configurable parameters of the UI chart and their default values.

| Parameter                                  | Description                                     | Default                                                                                              |
| ------------------------------------------ | ----------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| replicaCount                               | Number of UI pods to run                        | 1                                                                                                    |
| image.repository                           | UI image repository                             | public.ecr.aws/aws-containers/retail-store-sample-ui                                                 |
| image.pullPolicy                           | Image pull policy                               | IfNotPresent                                                                                         |
| image.tag                                  | Image tag                                       | -                                                                                                    |
| imagePullSecrets                           | Image pull secrets                              | []                                                                                                   |
| nameOverride                               | Override the name of the chart                  | ""                                                                                                   |
| fullnameOverride                           | Override the full name of the resources         | ""                                                                                                   |
| serviceAccount.create                      | Create service account                          | true                                                                                                 |
| serviceAccount.annotations                 | Service account annotations                     | {}                                                                                                   |
| serviceAccount.name                        | Service account name                            | ""                                                                                                   |
| podAnnotations                             | Pod annotations                                 | {}                                                                                                   |
| podSecurityContext                         | Pod security context                            | fsGroup: 1000                                                                                        |
| securityContext.capabilities.drop          | Security context capabilities to drop           | ["ALL"]                                                                                              |
| securityContext.capabilities.add           | Security context capabilities to add            | ["NET_BIND_SERVICE"]                                                                                 |
| securityContext.readOnlyRootFilesystem     | Mount root filesystem read-only                 | true                                                                                                 |
| securityContext.runAsNonRoot               | Run container as non-root user                  | true                                                                                                 |
| securityContext.runAsUser                  | User ID to run container                        | 1000                                                                                                 |
| service.type                               | Kubernetes Service type                         | ClusterIP                                                                                            |
| service.port                               | Service port                                    | 80                                                                                                   |
| service.annotations                        | Service annotations                             | {}                                                                                                   |
| service.loadBalancerClass                  | Class of load balancer to use                   | ""                                                                                                   |
| service.nodePort                           | Node port for service                           | -                                                                                                    |
| resources.limits.memory                    | Memory limit                                    | 512Mi                                                                                                |
| resources.requests.cpu                     | CPU request                                     | 128m                                                                                                 |
| resources.requests.memory                  | Memory request                                  | 512Mi                                                                                                |
| autoscaling.enabled                        | Enable HPA                                      | false                                                                                                |
| autoscaling.minReplicas                    | Minimum number of replicas                      | 1                                                                                                    |
| autoscaling.maxReplicas                    | Maximum number of replicas                      | 10                                                                                                   |
| autoscaling.targetCPUUtilizationPercentage | Target CPU utilization                          | 50                                                                                                   |
| nodeSelector                               | Node labels for pod assignment                  | {}                                                                                                   |
| tolerations                                | Pod tolerations                                 | []                                                                                                   |
| affinity                                   | Pod affinity                                    | {}                                                                                                   |
| topologySpreadConstraints                  | Pod topology spread constraints                 | []                                                                                                   |
| metrics.enabled                            | Enable metrics collection                       | true                                                                                                 |
| metrics.podAnnotations                     | Metrics pod annotations                         | prometheus.io/scrape: "true", prometheus.io/port: "8080", prometheus.io/path: "/actuator/prometheus" |
| configMap.create                           | Create ConfigMap                                | true                                                                                                 |
| configMap.name                             | Name of the ConfigMap                           | Generated if not set                                                                                 |
| app.theme                                  | Application theme (e.g., default, orange, dark) | default                                                                                              |
| app.endpoints.catalog                      | URL for the catalog service                     | http://catalog:80                                                                                    |
| app.endpoints.carts                        | URL for the carts service                       | http://carts:80                                                                                      |
| app.endpoints.orders                       | URL for the orders service                      | http://orders:80                                                                                     |
| app.endpoints.checkout                     | URL for the checkout service                    | http://checkout:80                                                                                   |
| app.chat.enabled                           | Enable chat feature                             | false                                                                                                |
| app.chat.provider                          | Chat provider configuration                     | ""                                                                                                   |
| app.chat.model                             | Chat model configuration                        | ""                                                                                                   |
| ingress.enabled                            | Enable ingress                                  | false                                                                                                |
| ingress.className                          | Ingress class name                              | ""                                                                                                   |
| ingress.annotations                        | Ingress annotations                             | {}                                                                                                   |
| ingress.tls                                | Ingress TLS configuration                       | []                                                                                                   |
| ingress.hosts                              | Ingress hosts configuration                     | []                                                                                                   |
| ingresses                                  | Additional ingress configurations               | []                                                                                                   |
| istio.enabled                              | Enable Istio integration                        | false                                                                                                |
| istio.hosts                                | Istio virtual service hosts                     | []                                                                                                   |
| opentelemetry.enabled                      | Enable OpenTelemetry                            | false                                                                                                |
| opentelemetry.instrumentation              | OpenTelemetry instrumentation configuration     | ""                                                                                                   |
| podDisruptionBudget.enabled                | Enable PodDisruptionBudget                      | false                                                                                                |
| podDisruptionBudget.minAvailable           | Minimum available pods                          | 2                                                                                                    |
| podDisruptionBudget.maxUnavailable         | Maximum unavailable pods                        | 1                                                                                                    |

## Examples

### Enabling Ingress

```yaml
ingress:
  enabled: true
  className: nginx
  hosts:
    - host: chart-example.local
      paths:
        - path: /
          pathType: Prefix
```

### Setting Resource Limits

```yaml
resources:
  limits:
    cpu: 100m
    memory: 128Mi
  requests:
    cpu: 100m
    memory: 128Mi
```

### Configuring Services

```yaml
app:
  endpoints:
    catalog: http://catalog:80
    carts: http://carts:80
    orders: http://orders:80
    checkout: http://checkout:80
```

### Changing App Theme

```yaml
app:
  theme: orange # Available options: default, dark, orange
```

### Enabling Chat Feature

```yaml
app:
  chat:
    enabled: true
    provider: "openai"
    model: "gpt-3.5-turbo"
```
