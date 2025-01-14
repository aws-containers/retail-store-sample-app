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
