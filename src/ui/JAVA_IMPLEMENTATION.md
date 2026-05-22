# Java Implementation - Custom OpenAI-Compatible API

This document explains how the Java code is configured to work with custom OpenAI-compatible APIs.

## Architecture Overview

The chat feature uses Spring AI's OpenAI integration, which supports any OpenAI-compatible API endpoint. Here's the flow:

```
Environment Variables / Application Properties
        ↓
ChatProperties (enabled, provider, model, etc.)
OpenAIChatProperties (base-url, api-key)
        ↓
OpenAIChatConfig (builds ChatClient)
        ↓
ChatController (/chat/submit endpoint)
        ↓
Frontend chat widget
```

## Configuration Classes

### 1. ChatProperties
**File**: `src/main/java/com/amazon/sample/ui/config/chat/ChatProperties.java`

Loads general chat configuration:

```java
@Configuration
@ConfigurationProperties(ChatProperties.PREFIX)  // Maps to "retail.ui.chat"
@Data
@ConditionalOnProperty(
  prefix = ChatProperties.PREFIX,
  name = "enabled",
  havingValue = "true"
)
public class ChatProperties {
  private String provider;      // "openai", "bedrock", "mock"
  private String prompt;        // System prompt for the model
  private String model;         // Model name
  private double temperature;   // Model temperature (0-1)
  private int maxTokens;        // Max response tokens
}
```

**Environment Variables Loaded**:
- `RETAIL_UI_CHAT_ENABLED` → `enabled`
- `RETAIL_UI_CHAT_PROVIDER` → `provider`
- `RETAIL_UI_CHAT_MODEL` → `model`
- `RETAIL_UI_CHAT_TEMPERATURE` → `temperature`
- `RETAIL_UI_CHAT_MAX_TOKENS` → `max-tokens`

### 2. OpenAIChatProperties
**File**: `src/main/java/com/amazon/sample/ui/config/chat/OpenAIChatProperties.java`

Loads OpenAI-specific configuration:

```java
@Configuration
@ConfigurationProperties(OpenAIChatProperties.PREFIX)  // Maps to "retail.ui.chat.openai"
@Data
public class OpenAIChatProperties {
  private String baseUrl;   // Custom API endpoint (e.g., https://api.ai.kodekloud.com)
  private String apiKey;    // API authentication key
}
```

**Environment Variables Loaded**:
- `RETAIL_UI_CHAT_OPENAI_BASE_URL` → `baseUrl`
- `RETAIL_UI_CHAT_OPENAI_API_KEY` → `apiKey`

### 3. OpenAIChatConfig
**File**: `src/main/java/com/amazon/sample/ui/config/chat/OpenAIChatConfig.java`

This is where the magic happens! It builds the ChatClient with custom endpoint:

```java
@Configuration
@ConditionalOnBean(ChatProperties.class)
@ConditionalOnProperty(
  prefix = ChatProperties.PREFIX,
  name = "provider",
  havingValue = "openai"  // Only loads if provider=openai
)
public class OpenAIChatConfig {

  @Bean
  public ChatClient chatClient(
    ChatProperties properties,
    OpenAIChatProperties openaiProperties
  ) {
    // Log the configuration being used
    log.warn("Creating OpenAI chat client with baseUrl: {}", 
      openaiProperties.getBaseUrl());

    // Create model options from ChatProperties
    var modelOptions = OpenAiChatOptions.builder()
      .model(properties.getModel())                    // e.g., "deepseek/deepseek-v3.2"
      .temperature(properties.getTemperature())        // e.g., 0.6
      .maxTokens(properties.getMaxTokens())            // e.g., 300
      .build();

    // Build the OpenAiApi with custom base URL
    OpenAiApi.Builder apiBuilder = OpenAiApi.builder()
      .apiKey(openaiProperties.getApiKey());          // Your API key

    // Set custom base URL if provided
    if (openaiProperties.getBaseUrl() != null && !openaiProperties.getBaseUrl().isEmpty()) {
      log.info("Using custom OpenAI base URL: {}", openaiProperties.getBaseUrl());
      apiBuilder.baseUrl(openaiProperties.getBaseUrl());  // e.g., https://api.ai.kodekloud.com
    }

    // Create the chat model with the custom endpoint
    var chatModel = OpenAiChatModel.builder()
      .openAiApi(apiBuilder.build())
      .defaultOptions(modelOptions)
      .build();

    // Return the ChatClient for use in controllers
    return ChatClient.create(chatModel);
  }
}
```

## How It Works

When you set these environment variables:

```bash
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE
```

The following happens at startup:

1. **Spring Boot loads properties** from environment variables
2. **ChatProperties bean is created** with `enabled=true, provider=openai, model=deepseek/deepseek-v3.2`
3. **OpenAIChatProperties bean is created** with `baseUrl=https://api.ai.kodekloud.com, apiKey=sk-...`
4. **OpenAIChatConfig is evaluated**:
   - The `@ConditionalOnProperty(name="provider", havingValue="openai")` is true
   - So the OpenAI configuration class is loaded
5. **ChatClient bean is created** with:
   - Custom API endpoint (`https://api.ai.kodekloud.com`)
   - Your API key
   - Model name and parameters
6. **ChatController gets the ChatClient** via dependency injection
7. **Frontend can now use `/chat/submit` endpoint**

## ChatController
**File**: `src/main/java/com/amazon/sample/ui/web/ChatController.java`

This is the REST endpoint that the frontend calls:

```java
@RestController
@RequestMapping("/chat")
@ConditionalOnProperty(prefix = "retail.ui.chat", name = "enabled")
public class ChatController {

  @Value("${retail.ui.chat.prompt}")  // Load system prompt from config
  private String systemPrompt;

  @Autowired
  private ChatClient client;  // Injected ChatClient (built by OpenAIChatConfig)

  @PostMapping("/submit")
  public Flux<ServerSentEvent<String>> streamEvents(
    @RequestBody ChatRequest request
  ) {
    // Use the ChatClient to call the custom API
    return this.client.prompt(request.getMessage())
      .system(this.systemPrompt)
      .stream()
      .content()
      .map(c -> {
        // Stream response back to frontend as Server-Sent Events
        return ServerSentEvent.<String>builder()
          .data(objectMapper.writeValueAsString(new ResponseMessage(c)))
          .build();
      });
  }
}
```

## Data Flow Example

When a user sends a message through the UI:

```
Frontend: POST /chat/submit { "message": "Hello" }
    ↓
ChatController.streamEvents()
    ↓
ChatClient.prompt("Hello")
    ↓
OpenAiChatModel uses OpenAiApi with custom baseUrl
    ↓
HTTP POST to: https://api.ai.kodekloud.com/chat/completions
    ├─ Headers:
    │  ├─ Authorization: Bearer sk-YOUR-API-KEY-HERE
    │  └─ Content-Type: application/json
    └─ Body:
       ├─ model: deepseek/deepseek-v3.2
       ├─ messages: [{ role: "user", content: "Hello" }]
       ├─ temperature: 0.6
       └─ max_tokens: 300
    ↓
KodeKloud API processes with Claude model
    ↓
Response with AI-generated text
    ↓
Server-Sent Events stream back to frontend
    ↓
Frontend displays response
```

## Key Points

1. **Conditional Configuration**: Only loads OpenAI config if `provider=openai`
2. **Custom Base URL**: The `apiBuilder.baseUrl()` method overrides the default OpenAI endpoint
3. **API Key**: Passed via Spring AI's built-in mechanisms
4. **Model Parameters**: Configurable via environment variables
5. **Error Handling**: Spring AI handles authentication failures, network errors, etc.

## Extension Points

You can extend this to:

1. **Add new providers**: Create `BedrockChatConfig`, `AnthropicChatConfig`, etc.
2. **Support multiple endpoints**: Store multiple base URLs and switch between them
3. **Add request/response logging**: Extend `OpenAIChatConfig` to add interceptors
4. **Implement conversation history**: Modify `ChatController` to track messages
5. **Add rate limiting**: Add Spring Cloud CircuitBreaker to protect the API

## Dependencies

The configuration depends on these Spring AI libraries (in `pom.xml`):

```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-openai</artifactId>
</dependency>
```

This provides:
- `OpenAiChatModel`: Chat model implementation
- `OpenAiApi`: API client with customizable base URL
- `OpenAiChatOptions`: Configuration for temperature, max tokens, etc.

## Troubleshooting

### Issue: ChatClient Bean Not Created

**Cause**: `provider` property not set to `openai`

**Solution**: Verify `RETAIL_UI_CHAT_PROVIDER=openai`

### Issue: "Using custom OpenAI base URL" Log Not Appearing

**Cause**: `baseUrl` is null or empty

**Solution**: Set `RETAIL_UI_CHAT_OPENAI_BASE_URL`

### Issue: Authentication Error

**Cause**: Invalid API key

**Solution**: Verify `RETAIL_UI_CHAT_OPENAI_API_KEY` is correct

### Issue: Model Not Found

**Cause**: Model name doesn't exist at the endpoint

**Solution**: Verify `RETAIL_UI_CHAT_MODEL` matches available models

---

**For more details**, refer to the Spring AI documentation:
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI Integration](https://docs.spring.io/spring-ai/reference/1.0-SNAPSHOT/api/chat/openai-chat.html)
