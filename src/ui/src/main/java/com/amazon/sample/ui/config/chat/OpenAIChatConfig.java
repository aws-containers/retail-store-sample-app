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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConditionalOnBean(ChatProperties.class)
@ConditionalOnProperty(
  prefix = ChatProperties.PREFIX,
  name = "provider",
  havingValue = "openai"
)
public class OpenAIChatConfig {

  @Bean
  public ChatClient chatClient(
    ChatProperties properties,
    OpenAIChatProperties openaiProperties
  ) {
    log.warn("Creating OpenAI chat client");

    var modelOptions = OpenAiChatOptions.builder()
      .model(properties.getModel())
      .temperature(properties.getTemperature())
      .maxTokens(properties.getMaxTokens())
      .build();

    var chatModel = OpenAiChatModel.builder()
      .openAiApi(
        OpenAiApi.builder()
          .baseUrl(openaiProperties.getBaseUrl())
          .apiKey(openaiProperties.getApiKey())
          .build()
      )
      .defaultOptions(modelOptions)
      .build();
    return ChatClient.create(chatModel);
  }
}
