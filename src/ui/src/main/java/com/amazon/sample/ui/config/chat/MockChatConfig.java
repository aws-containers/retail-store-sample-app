package com.amazon.sample.ui.config.chat;

import com.amazon.sample.ui.chat.MockChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ConditionalOnProperty(
  prefix = "retail.ui.chat",
  name = "provider",
  havingValue = "mock"
)
public class MockChatConfig {

  @Bean
  public ChatClient chatClient(FunctionCallingOptions options) {
    log.warn("Creating mock chat client");

    return ChatClient.create(new MockChatModel());
  }
}
