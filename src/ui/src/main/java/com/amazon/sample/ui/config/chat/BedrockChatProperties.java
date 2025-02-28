package com.amazon.sample.ui.config.chat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(BedrockChatProperties.PREFIX)
@Data
public class BedrockChatProperties {

  public static final String PREFIX = "retail.ui.chat.bedrock";

  private String region;
}
