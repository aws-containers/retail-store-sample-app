# Retail Store Sample App - Load Generator

This is a utility component to generate synthetic load on the sample application, which is useful for scenarios such as autoscaling, observability and resiliency testing. 

There are two seperate approaches to using this [Artillery](https://github.com/artilleryio/artillery) based load generator. The Standard and [Playwright](https://playwright.dev/) augmented load generators. See below for the details of each option.

## Standard load generator

Standard load load generator: This is direct HTTP calls and is intended allow for a fully scripted and containerized deployment without the use of browser based interactive scripts.

It primarily consists of a scenario for [Artillery](https://github.com/artilleryio/artillery), as well a script to help run it.

## Usage

### Local

A convenience script is provided to make it easier to run the load generator on your local machine.

Run the following command for usage instructions:

```bash
bash scripts/run.sh --help
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
    image: public.ecr.aws/aws-containers/retail-store-sample-utils:load-gen.0.3.0
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


## Playwright augmented load generator
Playwright augmented load generator: This is intended to use [Playwright](https://playwright.dev/) to augment [Artillery](https://github.com/artilleryio/artillery) by running the tests through a browser experience and allows for rapid scaled testing through an ECS fargate deployment.

## Usage

### Local

1. Install AWS CLI - see instructions here: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
2. Install NVM: 

```bash
bash curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash
```
3. Install Node: 

```bash
bash nvm install node
```
4. Install Artillery: 

```bash
bash npm install -g artillery@latest
```
5. Install playwright dependencies: npx playwright install-deps

```bash
bash npx playwright install-deps
```

6. Set your url to the ARTILLERY_TARGET environment variable.

```bash
export ARTILLERY_TARGET='http://{your-elb-name}.{aws-region}.elb.amazonaws.com'
```

7. There are 2 script file pre-created for you to run. We will use retail-store-test.yml is the examples below but the same commands can be run with retail-store-stable-test.yml. The following is a description of the kinds of loads each test creates:

- retail-store-test.yml - this runs for a 1 minute of load. It builds up and has a spike of traffic at the end. 
- retail-store-stable-test.yml - this will generate traffic for 5 minutes and 30 seconds. It ramps up for the first 30 seconds then runs a steady load for 5 minutes.



8. Run Artillery locally. 

```bash
bash npx artillery run ./tests/retail-store-test.yml
```

8. Run Artillery in ECS Fargate. We have to pass in the target because the ARTILLERY_TARGET environment varible wont exist on the ECS task host.

```bash
bash artillery run-fargate ./tests/retail-store-test.yml --count 2 --target $ARTILLERY_TARGET
```
