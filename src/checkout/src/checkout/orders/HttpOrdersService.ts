import { Checkout } from '../models/Checkout';
import { ExistingOrder, OrdersApi } from '../../clients/orders/api';
import { IOrdersService } from './IOrdersService';

export class HttpOrdersService implements IOrdersService {

  private ordersApi : OrdersApi;

  constructor(endpoint: string) {
    this.ordersApi = new OrdersApi(endpoint);
  }

  async create(checkout : Checkout) : Promise<ExistingOrder> {
    return this.ordersApi.createOrder({
      email: checkout.request.customerEmail,
      firstName: "John",
      lastName: "Doe",
      items: []
    }).then((value) => {
      return value.body;
    });
  }
}