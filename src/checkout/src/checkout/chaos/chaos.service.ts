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

import { Injectable } from '@nestjs/common';

@Injectable()
export class ChaosService {
  private latency: number = 0;
  private errorStatus: number = 0;
  private isLatencyOn: boolean = false;
  private isErrorStatusOn: boolean = false;
  private isHealthy: boolean = true;

  setLatency(ms: number): void {
    this.latency = ms;
    this.isLatencyOn = true;
  }

  setErrorStatus(status: number): void {
    this.errorStatus = status;
    this.isErrorStatusOn = true;
  }

  disableLatency(): void {
    this.isLatencyOn = false;
  }

  disableErrorStatus(): void {
    this.isErrorStatusOn = false;
  }

  setHealth(healthy: boolean): void {
    this.isHealthy = healthy;
  }

  getStatus() {
    return {
      latency: {
        enabled: this.isLatencyOn,
        value: this.latency,
      },
      error_status: {
        enabled: this.isErrorStatusOn,
        code: this.errorStatus,
      },
    };
  }

  isSystemHealthy(): boolean {
    return this.isHealthy;
  }

  shouldApplyChaos(path: string): boolean {
    return !path.startsWith('/chaos');
  }

  getLatencyDelay(): number | null {
    return this.isLatencyOn ? this.latency : null;
  }

  getErrorStatus(): number | null {
    return this.isErrorStatusOn ? this.errorStatus : null;
  }
}
