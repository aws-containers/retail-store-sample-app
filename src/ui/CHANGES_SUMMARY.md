# Summary of Changes to UI Service for OpenAI Integration

## Overview
Updated the UI service to use the OpenAI SDK directly with support for custom base URLs (like KodeKloud's API endpoint) instead of using Spring AI's OpenAI integration.

## Files Modified

### 1. **pom.xml**
- Added OpenAI Java SDK dependency: `com.openai:openai-java:4.11.1`

### 2. **OpenAIChatConfig.java** (Updated)
- Replaced Spring AI's OpenAiChatModel with direct OpenAI SDK client
- Creates `OpenAIClient` bean with custom baseUrl configuration
- Creates `OpenAIChatService` bean that implements the new `ChatService` interface
- Properly logs the custom base URL being used

### 3. **OpenAIChatService.java** (New)
- Implements the `ChatService` interface
- Uses OpenAI SDK directly for API calls
- Supports custom base URLs via the OpenAIClient configuration
- Implements streaming responses character-by-character
- Properly handles system prompts and user messages
- Includes error handling and logging

### 4. **ChatService.java** (New Interface)
- Defines common interface for all chat providers
- Method: `Flux<String> streamChat(String userMessage, String systemPrompt)`
- Allows any provider (OpenAI, Bedrock, Mock) to be plugged in

### 5. **ChatController.java** (Updated)
- Changed from using Spring AI's `ChatClient` to using the `ChatService` interface
- Now injects `ChatService` bean instead of hardcoded `ChatClient`
- Maintains same streaming response format (Server-Sent Events)

### 6. **MockChatConfig.java** (Updated)
- Updated to create `ChatService` bean instead of Spring AI's `ChatClient`
- Uses new `MockChatService` for testing

### 7. **MockChatService.java** (New)
- Implements `ChatService` interface
- Returns mock responses for testing purposes

### 8. **BedrockChatConfig.java** (Updated)
- Updated to create `ChatService` bean
- Uses new `BedrockChatService` implementation

### 9. **BedrockChatService.java** (New)
- Implements `ChatService` interface
- Uses Spring AI's Bedrock integration
- Maintains compatibility with AWS Bedrock provider

## Key Features

### ✅ Custom Base URL Support
The new implementation supports any OpenAI-compatible API endpoint:
```java
OpenAIClient.Builder builder = new OpenAIClient.Builder()
  .apiKey(openaiProperties.getApiKey());

if (openaiProperties.getBaseUrl() != null && !openaiProperties.getBaseUrl().isEmpty()) {
  builder.baseUrl(openaiProperties.getBaseUrl());
}
```

### ✅ Configuration Management
Full support for environment variables:
- `RETAIL_UI_CHAT_ENABLED` - Enable/disable chat
- `RETAIL_UI_CHAT_PROVIDER` - Provider selection (openai/bedrock/mock)
- `RETAIL_UI_CHAT_OPENAI_BASE_URL` - Custom API endpoint
- `RETAIL_UI_CHAT_OPENAI_API_KEY` - API authentication
- `RETAIL_UI_CHAT_MODEL` - Model specification
- `RETAIL_UI_CHAT_TEMPERATURE` - Response creativity
- `RETAIL_UI_CHAT_MAX_TOKENS` - Response length

### ✅ Provider Flexibility
The `ChatService` interface allows seamless switching between:
1. OpenAI (with custom endpoints)
2. AWS Bedrock
3. Mock (for testing)

### ✅ Error Handling
- Comprehensive logging at DEBUG and ERROR levels
- Proper error propagation via Flux error channel
- Graceful handling of missing responses

## Testing the Setup

### 1. Build the project
```bash
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
./mvnw clean install
```

### 2. Set environment variables
```bash
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR_API_KEY_HERE
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```

### 4. Test the chat endpoint
```bash
curl -X POST http://localhost:8080/chat/submit \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, what is your favorite product?"}'
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Maven can't resolve OpenAI SDK | Run `./mvnw clean install` to download dependencies |
| "No qualifying bean" error | Ensure `RETAIL_UI_CHAT_ENABLED=true` is set |
| Connection refused | Verify `RETAIL_UI_CHAT_OPENAI_BASE_URL` is correct and accessible |
| 401 Unauthorized | Check your API key and endpoint authentication |
| 404 Model not found | Verify model name matches endpoint's available models |

## Additional Documentation

See `OPENAI_SETUP.md` for detailed setup instructions and configuration examples.
