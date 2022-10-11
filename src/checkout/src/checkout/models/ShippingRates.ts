import { Type } from 'class-transformer';
import { IsString, ValidateNested } from 'class-validator';
import { ShippingOption } from './ShippingOption';

export class ShippingRates {

  @IsString()
  shipmentId : string;

  @ValidateNested({ each: true })
  @Type(() => ShippingOption)
  rates : ShippingOption[];

}