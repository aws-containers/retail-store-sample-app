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

import { Inject, Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config'
import { Checkout } from './models/Checkout';
import { CheckoutRequest } from './models/CheckoutRequest';
import { IOrdersService } from './orders/IOrdersService';
import { serialize , deserialize} from 'class-transformer';
import { CheckoutSubmitted } from './models/CheckoutSubmitted';
import { IShippingService } from './shipping';
import { ICheckoutRepository } from './repositories';

@Injectable()
export class CheckoutService {
  constructor(private configService: ConfigService, 
    @Inject('CheckoutRepository') private checkoutRepository : ICheckoutRepository,
    @Inject('OrdersService') private ordersService : IOrdersService,
    @Inject('ShippingService') private shippingService : IShippingService) {}

  async get(customerId: string) : Promise<Checkout> {
    const json = await this.checkoutRepository.get(customerId);

    if(!json) {
      return null;
    }

    return deserialize(Checkout, json);
  }


  async update(customerId: string, request : CheckoutRequest) : Promise<Checkout> {
    const tax = request.shippingAddress ? Math.floor(request.subtotal * 0.05) : -1; // Hardcoded 5% tax for now
    const effectiveTax = tax == -1 ? 0 : tax;

    return this.shippingService.getShippingRates(request).then(async (shippingRates) => {
      let shipping = -1;

      if(shippingRates) {
        for ( let i = 0; i < shippingRates.rates.length; i++ ) {
          if(shippingRates.rates[i].token == request.deliveryOptionToken) {
            shipping = shippingRates.rates[i].amount;
          }
        }
      }

      const checkout : Checkout =  {
        shippingRates,
        request,
        paymentId: this.makeid(16),
        paymentToken: this.makeid(32),
        shipping,
        tax,
        total: request.subtotal + effectiveTax,
      };

      await this.checkoutRepository.set(customerId, serialize(checkout));

      return checkout;
    });
  }

  async submit(customerId: string) : Promise<CheckoutSubmitted> {
    let checkout = await this.get(customerId);

    if(!checkout) {
      throw new Error('Checkout not found');
    }

    let order = await this.ordersService.create(checkout);

    await this.checkoutRepository.remove(customerId);

    return Promise.resolve({
      orderId: order.id,
      customerEmail: checkout.request.customerEmail
    });
  }

  private makeid(length) {
    let result             = '';
    const characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    for ( let i = 0; i < length; i++ ) {
       result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  }
}
