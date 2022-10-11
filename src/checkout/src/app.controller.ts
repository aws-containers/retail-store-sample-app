import { Controller, Get, Inject } from '@nestjs/common';
import { HealthCheck, HealthCheckService } from '@nestjs/terminus';

@Controller()
export class AppController {
  constructor(
    private healthCheckService: HealthCheckService,
  ){}

  @Get('health')
  @HealthCheck()
  health() {
    return this.healthCheckService.check([]);
  }
}
