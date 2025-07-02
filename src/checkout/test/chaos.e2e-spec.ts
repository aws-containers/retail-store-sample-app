/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import request from 'supertest';
import { AppModule } from '../src/app.module';
import { ChaosService } from '../src/chaos/chaos.service';

describe('Chaos Integration Tests', () => {
  let app: INestApplication;
  let chaosService: ChaosService;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    chaosService = moduleFixture.get<ChaosService>(ChaosService);
    await app.init();
  });

  beforeEach(() => {
    // Reset chaos service state before each test
    chaosService.disableLatency();
    chaosService.disableErrorStatus();
    chaosService.setHealth(true);
  });

  afterAll(async () => {
    await app.close();
  });

  describe('Health Check', () => {
    it('should return DOWN when chaos is enabled', async () => {
      // When chaos is enabled
      await request(app.getHttpServer()).post('/chaos/health').expect(201);

      // Then health check should return DOWN
      const response = await request(app.getHttpServer())
        .get('/health')
        .expect(503);

      expect(response.body).toMatchObject({
        status: 'error',
        error: {
          chaos: {
            status: 'down',
            reason: 'Chaos failure enabled',
          },
        },
      });
    });

    it('should return UP when chaos is disabled', async () => {
      const response = await request(app.getHttpServer())
        .get('/health')
        .expect(200);

      expect(response.body).toMatchObject({
        status: 'ok',
        info: expect.any(Object),
      });
    });
  });

  describe('Latency', () => {
    it('should delay requests when latency is enabled', async () => {
      // Given latency is set to 1 second
      const latencyMs = 1000;
      await request(app.getHttpServer())
        .post(`/chaos/latency/${latencyMs}`)
        .expect(201);

      // When making a request
      const startTime = Date.now();
      await request(app.getHttpServer()).get('/checkout/123').expect(404); // Assuming endpoint doesn't exist

      const duration = Date.now() - startTime;
      expect(duration).toBeGreaterThanOrEqual(latencyMs);
    });

    it('should not delay chaos endpoints', async () => {
      // Given latency is set
      const latencyMs = 1000;
      await request(app.getHttpServer())
        .post(`/chaos/latency/${latencyMs}`)
        .expect(201);

      // When checking chaos status
      const startTime = Date.now();
      await request(app.getHttpServer()).delete('/chaos/latency').expect(200);

      const duration = Date.now() - startTime;
      expect(duration).toBeLessThan(latencyMs);
    });
  });

  describe('Error Status', () => {
    it('should return error status when enabled', async () => {
      // Given error status is set to 503
      await request(app.getHttpServer()).post('/chaos/status/503').expect(201);

      // When making a request
      await request(app.getHttpServer()).get('/checkout/123').expect(503);
    });

    it('should validate error status input', async () => {
      // When setting invalid status
      await request(app.getHttpServer()).post('/chaos/status/999').expect(400);
    });
  });

  describe('Input Validation', () => {
    it('should validate latency input', async () => {
      await request(app.getHttpServer())
        .post('/chaos/latency/-100')
        .expect(400);
    });

    it('should validate status code input', async () => {
      await request(app.getHttpServer()).post('/chaos/status/999').expect(400);
    });
  });

  describe('Chaos Status', () => {
    it('should return current chaos configuration', async () => {
      // Given some chaos settings
      await request(app.getHttpServer()).post('/chaos/latency/100').expect(201);

      await request(app.getHttpServer()).post('/chaos/status/503').expect(201);

      // When getting status
      const response = await request(app.getHttpServer())
        .get('/chaos/status')
        .expect(200);

      expect(response.body).toMatchObject({
        latency: {
          enabled: true,
          value: 100,
        },
        error_status: {
          enabled: true,
          code: 503,
        },
      });
    });
  });
});
