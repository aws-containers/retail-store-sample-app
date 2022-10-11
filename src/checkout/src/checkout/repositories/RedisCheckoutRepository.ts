import { ICheckoutRepository } from './ICheckoutRepository';
import { createClient, RedisClientType } from 'redis';
import { Injectable } from '@nestjs/common';

@Injectable()
export class RedisCheckoutRepository implements ICheckoutRepository {

  private _client : RedisClientType;

  private _readClient : RedisClientType;

  constructor(private url: string, private readerUrl: string) { }

  async client() {
    if(!this._client) {
      this._client = createClient({url: this.url});

      await this._client.connect()
    }

    return this._client;
  }

  async readClient() {
    if(!this._readClient) {
      this._readClient = createClient({url: this.readerUrl});

      await this._readClient.connect()
    }

    return this._readClient;
  }

  async get(key : string) : Promise<string> {
    const client = await this.readClient()

    return client.get(key);
  }

  async set(key : string, value : string) : Promise<string> {
    const client = await this.client()

    return client.set(key, value);
  }

  async remove(key : string) : Promise<void> {
    const client = await this.client()

    await client.del(key);

    return Promise.resolve(null);
  }

  async health() {
    // something like this:
    // https://github.com/dannydavidson/k8s-neo-api/blob/master/annotely-graph/apps/ops/health.js
    return true;
  }

}