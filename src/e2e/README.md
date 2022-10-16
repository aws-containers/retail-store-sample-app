# Retail Store Sample App - End-to-End Tests

This component provides a suite of end-to-end tests that exercise a broad set of functionality of the application as a whole. They are designed to be cross-cutting and treat the application as a black box rather than a set of distinct components.

The tests are executed using `protractor`, and will run inside a headless Chromium browser.

## Running

There are two ways to run the tests.

### Docker

This is the easiest way to run the tests:

```
./scripts/run-docker.sh 'http://endpoint:8080'
```

Where the parameter should be adjusted to point at the endpoint of the UI service. Use the `-h` flag to display complete documentation for the script.

To run against Docker Compose running locally:

```
./scripts/run-docker.sh --network docker-compose_default 'http://ui:8080'
```

### NPM

The tests can be run locally using NPM. To do so the following components must be installed:
- NodeJS >= 16 & NPM
- Chrome web browser

```
npm install

ENDPOINT='http://endpoint:8080' npm test
```

Where the `ENDPOINT` environment variable should be adjusted to point at the endpoint of the UI service.