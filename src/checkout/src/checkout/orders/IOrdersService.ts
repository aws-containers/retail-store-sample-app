import { ExistingOrder } from '../../clients/orders/api';
import { Checkout } from '../models/Checkout';

export interface IOrdersService {
  create(checkout : Checkout) : Promise<ExistingOrder>;
}