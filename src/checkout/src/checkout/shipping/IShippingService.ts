import { CheckoutRequest } from '../models/CheckoutRequest';
import { ShippingRates } from '../models/ShippingRates';

export interface IShippingService {
  getShippingRates(request : CheckoutRequest) : Promise<ShippingRates>;
}