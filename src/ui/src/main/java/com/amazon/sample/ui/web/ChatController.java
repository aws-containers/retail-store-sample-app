package com.amazon.sample.ui.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

@RestController
@RequestMapping("/chat")
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

    public String getText() {
      return text;
    }
  }

  @PostMapping("/submit")
  public Flux<ServerSentEvent<String>> streamEvents(
    @RequestBody ChatRequest request
  ) {
    var client = BedrockRuntimeAsyncClient.builder()
      .credentialsProvider(DefaultCredentialsProvider.create())
      .region(Region.US_WEST_2)
      .build();

    var objectMapper = new ObjectMapper();

    var modelId = "anthropic.claude-3-haiku-20240307-v1:0";

    var message = Message.builder()
      .content(ContentBlock.fromText(request.getMessage()))
      .role(ConversationRole.USER)
      .build();

    Sinks.Many<ServerSentEvent<String>> sink = Sinks.many()
      .unicast()
      .onBackpressureBuffer();

    var responseStreamHandler = ConverseStreamResponseHandler.builder()
      .subscriber(
        ConverseStreamResponseHandler.Visitor.builder()
          .onContentBlockDelta(chunk -> {
            var response = new ResponseMessage(chunk.delta().text());
            try {
              sink.tryEmitNext(
                ServerSentEvent.<String>builder()
                  .data(objectMapper.writeValueAsString(response))
                  .build()
              );
            } catch (JsonProcessingException e) {
              throw new RuntimeException("Failed to construct JSON");
            }
          })
          .build()
      )
      .onError(err ->
        System.err.printf("Can't invoke '%s': %s", modelId, err.getMessage())
      )
      .build();

    try {
      client
        .converseStream(
          r ->
            r
              .modelId(modelId)
              .messages(message)
              .inferenceConfig(config ->
                config.maxTokens(512).temperature(0.5F).topP(0.9F)
              ),
          responseStreamHandler
        )
        .get();
    } catch (Exception e) {
      System.err.printf(
        "Can't invoke '%s': %s",
        modelId,
        e.getCause().getMessage()
      );
    }

    return sink.asFlux();
  }
}
