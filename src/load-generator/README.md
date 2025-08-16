# Retail Store Sample App - Load Generator

This is a utility component to generate synthetic load on the sample application, which is useful for scenarios such as autoscaling, observability and resiliency testing. It primarily consists of a scenario for [Artillery](https://github.com/artilleryio/artillery), as well a script to help run it.

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
