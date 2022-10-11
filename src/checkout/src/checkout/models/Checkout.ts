import { Type } from 'class-transformer';
import { IsInt, IsString, Min, ValidateNested } from 'class-validator';
import { CheckoutRequest } from './CheckoutRequest';
import { ShippingRates } from './ShippingRates';

export class Checkout {

  @ValidateNested()
  @Type(() => CheckoutRequest)
  request : CheckoutRequest;

  @ValidateNested()
  @Type(() => ShippingRates)
  shippingRates : ShippingRates;

  @IsString()
  paymentId : string;

  @IsString()
  paymentToken : string;

  @IsInt()
  @Min(-1)
  shipping : number;

  @IsInt()
  @Min(-1)
  tax : number;

  @IsInt()
  @Min(-1)
  total : number;

}