import { Type } from 'class-transformer';
import { ValidateNested, IsEmail, IsInt, Min, IsOptional, IsString } from 'class-validator';

import { Item } from './Item';
import { ShippingAddress } from './ShippingAddress';

export class CheckoutRequest {

  @IsEmail()
  @IsOptional()
  customerEmail : string;

  @ValidateNested({ each: true })
  @Type(() => Item)
  items : Item[];

  @ValidateNested()
  @Type(() => ShippingAddress)
  @IsOptional()
  shippingAddress : ShippingAddress;

  @IsInt()
  @Min(0)
  subtotal : number;

  @IsString()
  @IsOptional()
  deliveryOptionToken : string;

}