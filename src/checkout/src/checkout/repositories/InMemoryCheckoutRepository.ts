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

import { Injectable } from '@nestjs/common';
import { ICheckoutRepository } from './ICheckoutRepository';

@Injectable()
export class InMemoryCheckoutRepository implements ICheckoutRepository {

  map = new Map<string, string>(); 

  async get(key : string) : Promise<string> {
    if(this.map.has(key)) {
      return Promise.resolve(this.map.get(key));
    }

    return null;
  }

  async set(key : string, value : string) : Promise<string> {
    this.map.set(key, value);

    return Promise.resolve(value);
  }

  async remove(key : string) : Promise<void> {
    this.map.delete(key);

    return Promise.resolve(null);
  }
}