import { IsNumber, IsString } from 'class-validator';

export class CheckoutSubmitted {

  @IsString()
  orderId : string;

  @IsString()
  customerEmail : string;

}