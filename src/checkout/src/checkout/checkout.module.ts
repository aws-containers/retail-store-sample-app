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
