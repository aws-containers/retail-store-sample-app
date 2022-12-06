import {
  CompositePropagator,
} from '@opentelemetry/core';
import { SimpleSpanProcessor } from '@opentelemetry/sdk-trace-base';
import { getNodeAutoInstrumentations } from '@opentelemetry/auto-instrumentations-node';
import { B3InjectEncoding, B3Propagator } from '@opentelemetry/propagator-b3';
import { NodeSDK } from '@opentelemetry/sdk-node';
import { AsyncLocalStorageContextManager } from '@opentelemetry/context-async-hooks';
import * as process from 'process';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-grpc';
import { awsEc2Detector } from '@opentelemetry/resource-detector-aws';
import { envDetector } from '@opentelemetry/resources';

const otelSDK = new NodeSDK({
  traceExporter: new OTLPTraceExporter(),
  spanProcessor: new SimpleSpanProcessor(
    new OTLPTraceExporter()
  ),
  contextManager: new AsyncLocalStorageContextManager(),
  resourceDetectors: [
    envDetector,
    awsEc2Detector,
  ],
  textMapPropagator: new CompositePropagator({
    propagators: [
      new B3Propagator(),
      new B3Propagator({
        injectEncoding: B3InjectEncoding.MULTI_HEADER,
      }),
    ],
  }),
  instrumentations: [getNodeAutoInstrumentations()],
});

export default otelSDK;

// You can also use the shutdown method to gracefully shut down the SDK before process shutdown
// or on some operating system signal.
process.on('SIGTERM', () => {
  otelSDK
    .shutdown()
    .then(
      () => console.log('SDK shut down successfully'),
      err => console.log('Error shutting down SDK', err)
    )
    .finally(() => process.exit(0));
});