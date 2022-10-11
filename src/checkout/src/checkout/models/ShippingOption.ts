import { IsInt, IsNumber, IsString } from 'class-validator';

export class ShippingOption {

  @IsString()
  name : string;

  @IsInt()
  amount : number;

  @IsString()
  token : string;

  @IsInt()
  estimatedDays : number;

}