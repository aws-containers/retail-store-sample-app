package com.amazon.sample.ui.config.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(AnthropicChatProperties.PREFIX)
@Data
public class AnthropicChatProperties {

  public static final String PREFIX = "retail.ui.chat.anthropic";

  private String apiKey;
}
