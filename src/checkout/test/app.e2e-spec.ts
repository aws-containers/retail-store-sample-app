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