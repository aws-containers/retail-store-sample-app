package com.amazon.sample.ui.config.chat;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(ChatProperties.PREFIX)
@Data
@ConditionalOnProperty(
  prefix = ChatProperties.PREFIX,
  name = "enabled",
  havingValue = "true",
  matchIfMissing = false
)
public class ChatProperties {

  public static final String PREFIX = "retail.ui.chat";

  private String provider;

  private String prompt;

  private String model;

  private double temperature;

  private int maxTokens;
}
