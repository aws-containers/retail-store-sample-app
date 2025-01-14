package com.amazon.sample.ui.chat;

import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

public class MockChatModel implements ChatModel {

  @Override
  public ChatResponse call(Prompt prompt) {
    return new ChatResponse(
      List.of(new Generation(new AssistantMessage("This is a mock response")))
    );
  }

  @Override
  public Flux<ChatResponse> stream(Prompt prompt) {
    return Flux.just(this.call(prompt));
  }
}
