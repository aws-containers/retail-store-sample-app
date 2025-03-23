/*
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

package com.amazon.sample.orders.chaos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChaosServiceIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testChaosStatus() {
    restTemplate.postForEntity("/chaos/status/503", null, Map.class);

    ResponseEntity<String> response = restTemplate.getForEntity(
      "/orders",
      String.class
    );
    assertThat(response.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
  }

  @Test
  void testChaosLatency() {
    int latencyMs = 1000;
    restTemplate.postForEntity("/chaos/latency/" + latencyMs, null, Map.class);

    long startTime = System.currentTimeMillis();
    restTemplate.getForEntity("/orders", String.class);
    long endTime = System.currentTimeMillis();

    long duration = endTime - startTime;
    assertThat(duration, greaterThanOrEqualTo((long) latencyMs));
  }
}
