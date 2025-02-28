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
import { Checkout } from './models/Checkout';
import { CheckoutRequest } from './models/CheckoutRequest';
import { IOrdersService } from './orders/IOrdersService';
import { serialize, deserialize } from 'class-transformer';
import { CheckoutSubmitted } from './models/CheckoutSubmitted';
import { IShippingService } from './shipping';
import { ICheckoutRepository } from './repositories';
import { Item } from './models/Item';
import { ShippingRates } from './models/ShippingRates';

@Injectable()
export class CheckoutService {
  constructor(
    @Inject('CheckoutRepository')
    private checkoutRepository: ICheckoutRepository,
    @Inject('OrdersService') private ordersService: IOrdersService,
    @Inject('ShippingService') private shippingService: IShippingService,
  ) {}

  async get(customerId: string): Promise<Checkout> {
    const json = await this.checkoutRepository.get(customerId);

    if (!json) {
      return null;
    }

    return deserialize(Checkout, json);
  }

  async update(
    customerId: string,
    request: CheckoutRequest,
  ): Promise<Checkout> {
    let subtotal = 0;

    const items: Item[] = request.items.map((item) => {
      const totalCost = item.price * item.quantity;
      subtotal += totalCost;

      return {
        ...item,
        totalCost,
      };
    });

    const tax = request.shippingAddress ? 5 : -1; // Hardcoded $10 tax for now
    const effectiveTax = tax == -1 ? 0 : tax;

    let shipping = -1;
    let shippingRates: ShippingRates = null;

    if (request.shippingAddress) {
      shippingRates = await this.shippingService.getShippingRates(request);

      if (shippingRates) {
        for (let i = 0; i < shippingRates.rates.length; i++) {
          if (shippingRates.rates[i].token == request.deliveryOptionToken) {
            shipping = shippingRates.rates[i].amount;
          }
        }
      }
    }

    const effectiveShipping = shipping == -1 ? 0 : shipping;

    const checkout: Checkout = {
      shippingRates,
      shippingAddress: request.shippingAddress,
      deliveryOptionToken: request.deliveryOptionToken,
      items,
      paymentId: this.makeid(16),
      paymentToken: this.makeid(32),
      subtotal,
      shipping,
      tax,
      total: subtotal + effectiveTax + effectiveShipping,
    };

    await this.checkoutRepository.set(customerId, serialize(checkout));

    return checkout;
  }

  async submit(customerId: string): Promise<CheckoutSubmitted> {
    const checkout = await this.get(customerId);

    if (!checkout) {
      throw new Error('Checkout not found');
    }

    const order = await this.ordersService.create(checkout);

    await this.checkoutRepository.remove(customerId);

    return Promise.resolve({
      orderId: order.id,
      email: checkout.shippingAddress.email,
      items: checkout.items,
      subtotal: checkout.subtotal,
      shipping: checkout.shipping,
      tax: checkout.tax,
      total: checkout.total,
    });
  }

  private makeid(length) {
    let result = '';
    const characters =
      'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  }
}
