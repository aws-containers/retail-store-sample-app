import { IsOptional, IsString } from 'class-validator';

export class ShippingAddress {

  @IsString()
  firstName : string;

  @IsString()
  lastName : string;

  @IsString()
  address1 : string;

  @IsString()
  @IsOptional()
  address2 : string;

  @IsString()
  city : string;

  @IsString()
  state : string;

  @IsString()
  zip : string;

}