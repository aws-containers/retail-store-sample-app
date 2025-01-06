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