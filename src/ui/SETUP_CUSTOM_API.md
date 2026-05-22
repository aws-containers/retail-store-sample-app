# Setting Up Custom OpenAI-Compatible API

This guide explains how to configure the retail UI to use a custom OpenAI-compatible API endpoint (like Claude API through KodeKloud or similar providers).

## Quick Start

### Option 1: Environment Variables (Recommended)

```bash
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE

./mvnw spring-boot:run
```

### Option 2: Application Properties

Create or update `src/main/resources/application-local.yml`:

```yaml
retail:
  ui:
    chat:
      enabled: true
      provider: openai
      model: deepseek/deepseek-v3.2
      temperature: 0.6
      max-tokens: 300
      openai:
        base-url: https://api.ai.kodekloud.com
        api-key: sk-YOUR-API-KEY-HERE
```

Then run with the local profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### Option 3: Command Line Arguments

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="\
  --retail.ui.chat.enabled=true \
  --retail.ui.chat.provider=openai \
  --retail.ui.chat.model=deepseek/deepseek-v3.2 \
  --retail.ui.chat.openai.base-url=https://api.ai.kodekloud.com \
  --retail.ui.chat.openai.api-key=sk-YOUR-API-KEY-HERE"
```

## Configuration Properties

### Core Chat Properties

| Property | Environment Variable | Description | Required |
|----------|----------------------|-------------|----------|
| `retail.ui.chat.enabled` | `RETAIL_UI_CHAT_ENABLED` | Enable chat feature | Yes |
| `retail.ui.chat.provider` | `RETAIL_UI_CHAT_PROVIDER` | Provider type: `openai`, `bedrock`, or `mock` | Yes |
| `retail.ui.chat.model` | `RETAIL_UI_CHAT_MODEL` | Model name (e.g., `deepseek/deepseek-v3.2`) | Yes |
| `retail.ui.chat.temperature` | `RETAIL_UI_CHAT_TEMPERATURE` | Model temperature (0-1) | No, default: 0.6 |
| `retail.ui.chat.max-tokens` | `RETAIL_UI_CHAT_MAX_TOKENS` | Max response tokens | No, default: 300 |

### OpenAI Provider Properties

| Property | Environment Variable | Description | Required |
|----------|----------------------|-------------|----------|
| `retail.ui.chat.openai.base-url` | `RETAIL_UI_CHAT_OPENAI_BASE_URL` | Custom API endpoint URL | Yes (for OpenAI) |
| `retail.ui.chat.openai.api-key` | `RETAIL_UI_CHAT_OPENAI_API_KEY` | API authentication key | Yes (for OpenAI) |

## Troubleshooting

### Chat Feature Not Working

1. **Verify environment variables are set**:
   ```bash
   echo $RETAIL_UI_CHAT_ENABLED
   echo $RETAIL_UI_CHAT_OPENAI_API_KEY
   ```

2. **Check application logs** for configuration issues:
   ```
   Looking for logs like: "Creating OpenAI chat client with baseUrl: https://api.ai.kodekloud.com"
   ```

3. **Test API connectivity** from command line:
   ```bash
   curl -X POST https://api.ai.kodekloud.com/chat/completions \
     -H "Authorization: Bearer sk-YOUR-API-KEY-HERE" \
     -H "Content-Type: application/json" \
     -d '{
       "model": "deepseek/deepseek-v3.2",
       "messages": [{"role": "user", "content": "Hello"}],
       "max_tokens": 300
     }'
   ```

### Common Issues

**Issue**: Chat endpoint returns 401 Unauthorized
- **Solution**: Verify your API key is correct and not expired

**Issue**: Chat endpoint returns 404 Not Found
- **Solution**: Verify the base URL is correct and includes the `/v1` path

**Issue**: Chat is not available in the UI
- **Solution**: Ensure `RETAIL_UI_CHAT_ENABLED=true` is set before starting the application

**Issue**: Connection timeout
- **Solution**: Check network connectivity to the base URL and verify firewall rules

## How It Works

The application uses Spring AI's OpenAI integration, which supports OpenAI-compatible APIs. The configuration allows you to:

1. Override the default OpenAI endpoint with a custom one
2. Use different models available through the custom endpoint
3. Adjust model parameters like temperature and max tokens

### Code Architecture

- **ChatProperties**: Loads general chat configuration
- **OpenAIChatProperties**: Loads OpenAI-specific configuration (base URL and API key)
- **OpenAIChatConfig**: Builds the ChatClient with the custom endpoint
- **ChatController**: Provides the `/chat/submit` endpoint for frontend interactions

## Testing the Integration

1. Start the application with chat enabled
2. Visit `http://localhost:8080` in your browser
3. Look for the chat widget in the UI
4. Send a test message and verify the response

## Docker Compose Setup

To run with Docker Compose and custom API:

1. Create a `.env` file in the UI directory:
   ```env
   RETAIL_UI_CHAT_ENABLED=true
   RETAIL_UI_CHAT_PROVIDER=openai
   RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
   RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
   RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE
   ```

2. Run Docker Compose:
   ```bash
   docker compose up
   ```

Docker Compose will automatically load the `.env` file and pass the variables to the container.

## Security Notes

- **Never commit API keys** to version control. Use environment variables or `.env` files (which should be in `.gitignore`)
- **Keep API keys secure** and rotate them regularly
- **Use HTTPS** for production endpoints
- **Monitor API usage** to detect unauthorized access

## Support

For issues with:
- **Spring AI**: Check [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- **OpenAI API compatibility**: Refer to your provider's API documentation
- **Application configuration**: Check the main README.md
