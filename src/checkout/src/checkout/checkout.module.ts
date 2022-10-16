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

import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { CheckoutController } from './checkout.controller';
import { CheckoutService } from './checkout.service';
import { MockOrdersService, HttpOrdersService } from './orders'
import { InMemoryCheckoutRepository, ICheckoutRepository, RedisCheckoutRepository } from './repositories';
import { MockShippingService } from './shipping';

const orderServiceProvider = {
  provide: 'OrdersService',
  useFactory: (configService: ConfigService) => {
    const ordersEndpoint = configService.get('endpoints.orders')
    if(ordersEndpoint) {
      return new HttpOrdersService(ordersEndpoint);
    }
    return new MockOrdersService();
  },
  inject: [ConfigService],
};

const shippingServiceProvider = {
  provide: 'ShippingService',
  useFactory: () => {
    return new MockShippingService();
  },
};

const repositoryProvider = {
  provide: 'CheckoutRepository',
  useFactory: (configService: ConfigService) => {
    let redisUrl = configService.get('redis.url');
    let redisReaderUrl = configService.get('redis.reader.url');

    if(!redisReaderUrl) {
      redisReaderUrl = redisUrl;
    }

    let repository : ICheckoutRepository;

    if(redisUrl) {
      console.log('Creating RedisRepository...');
      repository = new RedisCheckoutRepository(redisUrl, redisReaderUrl);
    }
    else {
      console.log('Creating InMemoryRepository...');
      repository = new InMemoryCheckoutRepository();
    }

    return repository;
  },
  inject: [ConfigService],
};

@Module({
  imports: [ConfigModule],
  controllers: [CheckoutController],
  providers: [orderServiceProvider, shippingServiceProvider, repositoryProvider, CheckoutService],
})
export class CheckoutModule {}
