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

import { Controller, Get } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { HealthCheck, HealthCheckService } from '@nestjs/terminus';
import { ChaosHealthIndicator } from './checkout/chaos/chaos.health';

@Controller()
export class AppController {
  constructor(
    private healthCheckService: HealthCheckService,
    private chaosHealthIndicator: ChaosHealthIndicator,
    private configService: ConfigService,
  ) {}

  @Get('health')
  @HealthCheck()
  health() {
    return this.healthCheckService.check([
      () => this.chaosHealthIndicator.isHealthy(),
    ]);
  }

  @Get('topology')
  @HealthCheck()
  topology() {
    const persistenceProvider = this.configService.get('persistence.provider');
    let databaseEndpoint = 'N/A';

    if (persistenceProvider === 'redis') {
      databaseEndpoint = this.configService.get('persistence.redis.url');
    }

    return {
      persistenceProvider,
      databaseEndpoint,
    };
  }
}
