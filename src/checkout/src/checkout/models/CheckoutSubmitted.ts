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

import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { IsInt, IsString, Min, ValidateNested } from 'class-validator';
import { Item } from './Item';

export class CheckoutSubmitted {
  @IsString()
  @ApiProperty()
  orderId: string;

  @IsString()
  @ApiProperty()
  email: string;

  @ValidateNested({ each: true })
  @Type(() => Item)
  @ApiProperty({ type: [Item] })
  items: Item[];

  @IsInt()
  @Min(0)
  @ApiProperty({ type: 'integer' })
  subtotal: number;

  @ApiProperty({ type: 'integer' })
  @IsInt()
  @Min(-1)
  shipping: number;

  @ApiProperty({ type: 'integer' })
  @IsInt()
  @Min(-1)
  tax: number;

  @ApiProperty({ type: 'integer' })
  @IsInt()
  @Min(-1)
  total: number;
}
