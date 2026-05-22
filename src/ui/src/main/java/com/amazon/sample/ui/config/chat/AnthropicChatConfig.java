package com.amazon.sample.ui.config.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
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
  havingValue = "anthropic"
)
public class AnthropicChatConfig {

  @Bean
  public ChatClient chatClient(
    ChatProperties properties,
    AnthropicChatProperties anthropicProperties
  ) {
    log.warn("Creating Anthropic chat client with model: {}", properties.getModel());

    var api = AnthropicApi.builder()
      .apiKey(anthropicProperties.getApiKey())
      .build();

    var modelOptions = AnthropicChatOptions.builder()
      .model(properties.getModel())
      .temperature(properties.getTemperature())
      .maxTokens(properties.getMaxTokens())
      .build();

    var chatModel = AnthropicChatModel.builder()
      .anthropicApi(api)
      .defaultOptions(modelOptions)
      .build();

    return ChatClient.create(chatModel);
  }
}
