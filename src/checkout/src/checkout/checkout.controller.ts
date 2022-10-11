import { Body, Controller, Get, Inject, NotFoundException, Param, Post } from '@nestjs/common';
import { CheckoutService } from './checkout.service';
import { Checkout } from './models/Checkout';
import { CheckoutRequest } from './models/CheckoutRequest';
import { CheckoutSubmitted } from './models/CheckoutSubmitted';

@Controller('checkout')
export class CheckoutController {
  constructor(private readonly checkoutService: CheckoutService) {}

  /*@Get()
  getHello(): string {
    this.appService
    return this.appService.getHello();
  }*/

  @Get(':customerId')
  async getCheckout(@Param('customerId') customerId: string) : Promise<Checkout> {
    const checkout = this.checkoutService.get(customerId);

    return checkout.then(function(data) {
      if(!data) {
        throw new NotFoundException('Checkout not found');
      }

      return data;
    });
  }

  @Post(':customerId/update')
  async updateCheckout(@Param('customerId') customerId: string, @Body() request: CheckoutRequest) : Promise<Checkout> {
    return this.checkoutService.update(customerId, request);
  }

  @Post(':customerId/submit')
  async submitCheckout(@Param('customerId') customerId: string) : Promise<CheckoutSubmitted> {
    return this.checkoutService.submit(customerId);
  }
}
