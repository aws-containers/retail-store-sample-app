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

export default () => ({
  persistence: {
    provider: process.env.RETAIL_CHECKOUT_PERSISTENCE_PROVIDER || 'in-memory',
    redis: {
      url: process.env.RETAIL_CHECKOUT_PERSISTENCE_REDIS_URL || '',
      reader: {
        url: process.env.RETAIL_CHECKOUT_PERSISTENCE_REDIS_READER_URL || '',
      },
    },
  },
  endpoints: {
    orders: process.env.RETAIL_CHECKOUT_ENDPOINTS_ORDERS || '',
  },
  shipping: {
    prefix: process.env.RETAIL_CHECKOUT_SHIPPING_NAME_PREFIX || '',
  },
});
