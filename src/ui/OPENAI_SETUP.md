# OpenAI Integration Setup Guide for Retail Store Sample App - UI Service

## Overview

The UI service has been updated to use the OpenAI SDK directly with support for custom endpoints (like KodeKloud's API endpoint). This guide explains the configuration and how to set it up.

## Environment Variables

Configure the following environment variables to use OpenAI with a custom endpoint:

```bash
# Enable the chat feature
export RETAIL_UI_CHAT_ENABLED=true

# Set provider to openai
export RETAIL_UI_CHAT_PROVIDER=openai

# Set the OpenAI model
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2

# Set the custom base URL (for KodeKloud or other OpenAI-compatible APIs)
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com

# Set your API key
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR_API_KEY_HERE

# Optional: Set temperature and max tokens (defaults are 0.6 and 300)
export RETAIL_UI_CHAT_TEMPERATURE=0.6
export RETAIL_UI_CHAT_MAX_TOKENS=300
```

## Configuration Example

If using `application.yml` or `application-prod.yml`:

```yaml
retail:
  ui:
    chat:
      enabled: true
      provider: openai
      model: deepseek/deepseek-v3.2
      temperature: 0.6
      max-tokens: 300
      prompt: |
        You are A.G.E.N.T., a sarcastic AI assistant...
      openai:
        base-url: https://api.ai.kodekloud.com
        api-key: sk-YOUR_API_KEY_HERE
```

## Running the Application

### Local Development

```bash
# Set environment variables
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR_API_KEY_HERE

# Run the application
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
./mvnw spring-boot:run
```

### Docker

```bash
docker compose up
```

With environment variables:

```bash
docker compose -f docker-compose.yml \
  -e RETAIL_UI_CHAT_ENABLED=true \
  -e RETAIL_UI_CHAT_PROVIDER=openai \
  -e RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2 \
  -e RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com \
  -e RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR_API_KEY_HERE \
  up
```

## Architecture Changes

### New Classes Created

1. **ChatService.java** - Interface for all chat providers
   - Methods: `streamChat(userMessage, systemPrompt)`

2. **OpenAIChatService.java** - OpenAI SDK implementation
   - Uses `com.openai:openai-java` SDK
   - Supports custom base URLs
   - Handles streaming responses

3. **MockChatService.java** - Mock implementation for testing
   - Returns static test responses

4. **BedrockChatService.java** - AWS Bedrock implementation
   - Uses Spring AI for Bedrock integration

### Updated Classes

1. **OpenAIChatConfig.java**
   - Creates `OpenAIClient` bean with custom baseUrl and apiKey
   - Creates `OpenAIChatService` bean

2. **BedrockChatConfig.java**
   - Updated to create `BedrockChatService` bean

3. **MockChatConfig.java**
   - Updated to create `MockChatService` bean

4. **ChatController.java**
   - Changed to use `ChatService` interface instead of Spring AI's `ChatClient`
   - Injects `ChatService` bean dynamically based on provider

### Dependencies Added

- `com.openai:openai-java:4.11.1` - OpenAI Java SDK

## Testing the Integration

### Test Endpoint

The chat API endpoint is available at:

```
POST /chat/submit
```

Request body:
```json
{
  "message": "What is your favorite product?"
}
```

Response: Server-Sent Events (SSE) stream with character-by-character response.

### Troubleshooting

1. **"No qualifying bean of type 'ChatService'"**
   - Ensure `RETAIL_UI_CHAT_ENABLED=true` and `RETAIL_UI_CHAT_PROVIDER` is set correctly

2. **Connection refused to base URL**
   - Verify the `RETAIL_UI_CHAT_OPENAI_BASE_URL` is correct
   - Check if the endpoint is reachable

3. **Unauthorized (401) error**
   - Verify the API key is correct
   - Check if the API key has the right permissions

4. **Model not found (404) error**
   - Verify the model name matches what's available in your API endpoint
   - For KodeKloud, ensure you're using a supported model

## Providers Supported

1. **openai** - Uses OpenAI SDK with custom endpoint support
2. **mock** - Mock provider for testing
3. **bedrock** - AWS Bedrock provider

Select the provider via `RETAIL_UI_CHAT_PROVIDER` environment variable.

## Notes

- The implementation uses Reactor/Project Reactor for non-blocking, reactive streaming
- Responses are streamed character-by-character via Server-Sent Events
- The system prompt is configurable and sent with each request
- Temperature and max tokens are configurable and sent with each request
