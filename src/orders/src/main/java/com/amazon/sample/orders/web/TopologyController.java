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

package com.amazon.sample.orders.web;

import com.amazon.sample.orders.config.DatabaseProperties;
import com.amazon.sample.orders.config.messaging.RabbitMQProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/topology")
@Slf4j
public class TopologyController {

  @Value("${retail.orders.persistence.provider}")
  private String persistenceProvider;

  @Value("${retail.orders.messaging.provider}")
  private String messagingProvider;

  @Autowired(required = false)
  private DatabaseProperties databaseProperties;

  @Autowired(required = false)
  private RabbitMQProperties rabbitMQProperties;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, String> info() {
    Map<String, String> info = new HashMap<>();
    info.put("persistenceProvider", persistenceProvider);
    info.put("databaseEndpoint", "N/A");
    info.put("messagingEndpoint", "N/A");

    if (persistenceProvider.equals("postgres")) {
      info.put("databaseEndpoint", this.databaseProperties.getEndpoint());
    }

    info.put("messagingProvider", messagingProvider);

    if (messagingProvider.equals("rabbitmq")) {
      info.put(
        "messagingEndpoint",
        String.join(",", this.rabbitMQProperties.getAddresses())
      );
    }
    return info;
  }
}
