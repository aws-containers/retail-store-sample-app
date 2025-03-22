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

import {
  Controller,
  Post,
  Delete,
  Get,
  Param,
  BadRequestException,
} from '@nestjs/common';
import { ChaosService } from './chaos.service';

@Controller('chaos')
export class ChaosController {
  constructor(private chaosService: ChaosService) {}

  @Post('latency/:ms')
  setLatency(@Param('ms') ms: string) {
    const latency = parseInt(ms, 10);
    if (isNaN(latency) || latency < 0) {
      throw new BadRequestException(
        'Invalid latency value. Please provide a positive integer in milliseconds.',
      );
    }
    this.chaosService.setLatency(latency);
    return { message: `Latency set to ${ms}ms` };
  }

  @Post('status/:code')
  setErrorStatus(@Param('code') code: string) {
    const status = parseInt(code, 10);
    if (isNaN(status) || status < 100 || status > 599) {
      throw new BadRequestException(
        'Invalid HTTP status code. Please provide a valid status code (100-599).',
      );
    }
    this.chaosService.setErrorStatus(status);
    return { message: `Error status code set to ${code}` };
  }

  @Delete('latency')
  disableLatency() {
    this.chaosService.disableLatency();
    return { message: 'Latency disabled' };
  }

  @Delete('status')
  disableErrorStatus() {
    this.chaosService.disableErrorStatus();
    return { message: 'Error status disabled' };
  }

  @Get('status')
  getStatus() {
    return this.chaosService.getStatus();
  }

  @Post('health')
  setHealth() {
    this.chaosService.setHealth(false);
    return { message: 'Health check endpoint disabled' };
  }

  @Delete('health')
  enableHealth() {
    this.chaosService.setHealth(true);
    return { message: 'Health endpoint enabled' };
  }
}
