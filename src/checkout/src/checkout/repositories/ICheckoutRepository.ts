export interface ICheckoutRepository {
  get(key : string) : Promise<string>;
  set(key : string, value : string) : Promise<string>;
  remove(key : string) : Promise<void>;
}