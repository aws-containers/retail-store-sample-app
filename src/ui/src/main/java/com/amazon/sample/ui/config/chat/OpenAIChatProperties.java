package com.amazon.sample.ui.config.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(OpenAIChatProperties.PREFIX)
@Data
public class OpenAIChatProperties {

  public static final String PREFIX = "retail.ui.chat.openai";

  private String baseUrl;

  private String apiKey;
}
