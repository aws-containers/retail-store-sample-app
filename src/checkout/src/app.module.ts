import { Module } from '@nestjs/common';
import configuration from './config/configuration';
import { AppController } from './app.controller';
import { ConfigModule } from '@nestjs/config';
import { TerminusModule } from '@nestjs/terminus';
import { CheckoutModule } from './checkout/checkout.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      load: [configuration]
    }),
    TerminusModule,
    CheckoutModule
  ],
  controllers: [AppController],
  providers: [],
})
export class AppModule {}
