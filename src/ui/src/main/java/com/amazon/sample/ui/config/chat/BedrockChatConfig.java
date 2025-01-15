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

package com.amazon.sample.ui.config.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Configuration
@Slf4j
@ConditionalOnProperty(
  prefix = "retail.ui.chat",
  name = "provider",
  havingValue = "bedrock"
)
public class BedrockChatConfig {

  @Value("${retail.ui.chat.bedrock.region}")
  private String region;

  @Bean
  public ChatClient chatClient(FunctionCallingOptions options) {
    log.warn("Creating Amazon Bedrock chat client");

    var chatModel = BedrockProxyChatModel.builder()
      .withCredentialsProvider(DefaultCredentialsProvider.create())
      .withRegion(Region.of(this.region))
      .withDefaultOptions(options)
      .build();

    return ChatClient.create(chatModel);
  }
}
