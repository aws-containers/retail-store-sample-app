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

package com.amazon.sample.orders.config.messaging;

import com.amazon.sample.orders.messaging.sqs.SqsMessagingProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration;
import io.awspring.cloud.autoconfigure.sqs.SqsProperties;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@ConditionalOnProperty(
  prefix = MessagingProperties.PREFIX,
  name = "provider",
  havingValue = "sqs"
)
@Slf4j
public class SqsMessagingConfig extends SqsAutoConfiguration {

  public SqsMessagingConfig(SqsProperties sqsProperties) {
    super(sqsProperties);
  }

  @Value("${retail.orders.messaging.sqs.topic}")
  private String messageQueueTopic;

  @Bean
  public SqsAsyncClient sqsQueue() {
    return SqsAsyncClient.builder().build();
  }

  @Bean
  public SqsMessagingProvider messagingProvider(
    SqsAsyncClient amazonSqs,
    ObjectMapper mapper
  ) {
    log.info("Creating SQS messaging provider");

    return new SqsMessagingProvider(
      messageQueueTopic,
      SqsTemplate.newSyncTemplate(amazonSqs),
      mapper
    );
  }
}
