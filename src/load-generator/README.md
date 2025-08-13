# Retail Store Sample App - Load Generator

This is a utility component to generate synthetic load on the sample application, which is useful for scenarios such as autoscaling, observability and resiliency testing. It primarily consists of a set of scenarios for [Artillery](https://github.com/artilleryio/artillery), as well as scripts to help run it.

## Usage

### Local

A convenience script is provided to make it easier to run the load generator on your local machine.

Run the following command for usage instructions:

```bash
bash scripts/run-docker.sh --help
```

### Kubernetes

You can easily run the load generator as one or more Pods in a Kubernetes cluster. For example:

(Note: Update `http://ui.ui.svc` to reflect your namespace structure)

```bash
$ cat <<'EOF' | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: load-generator
spec:
  containers:
  - name: artillery
    image: artilleryio/artillery:2.0.22
    args:
    - "run"
    - "-t"
    - "http://ui.ui.svc"
    - "/scripts/scenario.yml"
    volumeMounts:
    - name: scripts
      mountPath: /scripts
  initContainers:
  - name: setup
    image: public.ecr.aws/aws-containers/retail-store-sample-utils:load-gen.1.2.1 <!-- x-release-please-version -->
    command:
    - bash
    args:
    - -c
    - "cp /artillery/* /scripts"
    volumeMounts:
    - name: scripts
      mountPath: "/scripts"
  volumes:
  - name: scripts
    emptyDir: {}
EOF
```

Note: Ensure the image tag of `retail-store-sample-load-generator` matches the version of the application being targeted.
