import { Checkout } from '../models/Checkout';
import { ExistingOrder, Order, OrdersApi } from '../../clients/orders/api';
import { IOrdersService } from './IOrdersService';

export class MockOrdersService implements IOrdersService {

  constructor() {
  }

  async create(checkout : Checkout) : Promise<ExistingOrder> {
    return {
      id: "abc123",
      email: checkout.request.customerEmail
    };
  }
}