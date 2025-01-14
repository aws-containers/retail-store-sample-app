package com.amazon.sample.ui.config.chat;

import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "retail.ui.chat", name = "enabled")
public class ChatOptionsConfig {

  @Value("${retail.ui.chat.model}")
  private String model;

  @Value("${retail.ui.chat.temperature}")
  private double temperature;

  @Value("${retail.ui.chat.maxTokens}")
  private int maxTokens;

  @Bean
  public FunctionCallingOptions options() {
    return FunctionCallingOptions.builder()
      .model(this.model)
      .temperature(this.temperature)
      .maxTokens(this.maxTokens)
      .build();
  }
}
