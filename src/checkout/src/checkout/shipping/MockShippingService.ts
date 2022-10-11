import { CheckoutRequest } from '../models/CheckoutRequest';
import { ShippingRates } from '../models/ShippingRates';
import { IShippingService } from './IShippingService';

export class MockShippingService implements IShippingService {

  async getShippingRates(request : CheckoutRequest) : Promise<ShippingRates> {
    return Promise.resolve({
      shipmentId: this.makeid(32),
      rates: [{
        name: "Priority Mail",
        amount: 5,
        token: "priority-mail",
        estimatedDays: 10
      }, {
        name: "Priority Mail Express",
        amount: 15,
        token: "priority-mail-express",
        estimatedDays: 5
      }]
    });
  }

  private makeid(length) {
    let result             = '';
    const characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    for ( let i = 0; i < length; i++ ) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  }
}