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

import { Injectable, OnModuleDestroy } from '@nestjs/common';
import { ICheckoutRepository } from './ICheckoutRepository';
import { createClient, RedisClientType } from 'redis';

@Injectable()
export class RedisCheckoutRepository
  implements ICheckoutRepository, OnModuleDestroy
{
  private _client: RedisClientType;
  private _readClient: RedisClientType;

  constructor(
    private url: string,
    private readerUrl: string,
  ) {}

  async client() {
    if (!this._client) {
      this._client = createClient({ url: this.url });
      await this._client.connect();
    }
    return this._client;
  }

  async readClient() {
    if (!this._readClient) {
      this._readClient = createClient({ url: this.readerUrl });
      await this._readClient.connect();
    }
    return this._readClient;
  }

  // Implement onModuleDestroy to clean up Redis connections
  async onModuleDestroy() {
    try {
      if (this._client) {
        await this._client.quit();
        this._client = null;
      }

      if (this._readClient) {
        await this._readClient.quit();
        this._readClient = null;
      }
    } catch (error) {
      console.error('Error closing Redis connections:', error);
      throw error;
    }
  }

  async get(key: string): Promise<string> {
    const client = await this.readClient();

    //@ts-expect-error TODO: Change from redis client upgrade
    return client.get(key);
  }

  async set(key: string, value: string): Promise<string> {
    const client = await this.client();

    //@ts-expect-error TODO: Change from redis client upgrade
    return client.set(key, value);
  }

  async remove(key: string): Promise<void> {
    const client = await this.client();
    await client.del(key);
    return Promise.resolve(null);
  }

  async health() {
    try {
      const client = await this.client();
      await client.ping();
      return true;
    } catch (error) {
      console.error('Redis health check failed:', error);
      return false;
    }
  }
}
