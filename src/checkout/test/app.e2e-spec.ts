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

import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';

describe('AppController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  it('/health (GET)', () => {
    return request(app.getHttpServer())
      .get('/health')
      .expect(200);
  });

  it('/checkout/test123 (GET)', () => {
    return request(app.getHttpServer())
      .get('/checkout/test123')
      .expect(404);
  });

  it('/checkout/test124 (POST)', () => {
    return request(app.getHttpServer())
      .post('/checkout/test124/update')
      .send(valid)
      .expect(201);
  });
});

const valid = {
  customerEmail: 'asdasd@asdasd.com',
  items: [
      {
          id: 'a1',
          name: 'A1',
          quantity: 1,
          unitCost: 123,
          totalCost: 123,
          imageUrl: "localhost:8080/image.jpg"
      },
      {
        id: 'b1',
        name: 'B1',
        quantity: 1,
        unitCost: 123,
        totalCost: 123,
        imageUrl: "localhost:8080/image.jpg"
      }
  ],
  shippingAddress: {
      'firstName': 'John',
      'lastName': 'Doe',
      'address1': '999 Main St.',
      'address2': '#123',
      'city': 'Sometown',
      'state': 'AB',
      'zip': '12345'
  },
  subtotal: 492
};