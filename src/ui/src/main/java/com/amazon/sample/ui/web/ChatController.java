package com.amazon.sample.ui.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@ConditionalOnProperty(prefix = "retail.ui.chat", name = "enabled")
public class ChatController {

  private static class ChatRequest {

    @JsonProperty("message")
    private String message;

    public String getMessage() {
      return message;
    }

    @SuppressWarnings("unused")
    public void setMessage(String message) {
      this.message = message;
    }
  }

  private static class ResponseMessage {

    @JsonProperty("text")
    private String text;

    public ResponseMessage(String text) {
      this.text = text;
    }

    @SuppressWarnings("unused")
    public String getText() {
      return text;
    }
  }

  @Value("${retail.ui.chat.prompt}")
  private String systemPrompt;

  @Autowired
  private ChatClient client;

  @PostMapping("/submit")
  public Flux<ServerSentEvent<String>> streamEvents(
    @RequestBody ChatRequest request
  ) {
    var objectMapper = new ObjectMapper();

    return this.client.prompt(request.getMessage())
      .system(this.systemPrompt)
      .stream()
      .content()
      .map(c -> {
        try {
          var response = new ResponseMessage(c);
          return ServerSentEvent.<String>builder()
            .data(objectMapper.writeValueAsString(response))
            .build();
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to serialize chat response", e);
        }
      });
  }
}
