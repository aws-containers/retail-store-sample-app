# Testing Chat Integration

This guide provides step-by-step instructions for testing the chat feature with a custom OpenAI-compatible API.

## Prerequisites

- Java 17+ installed
- Maven installed
- Valid API key for custom endpoint
- Access to the custom API endpoint

## Step 1: Verify Environment Variables

Before starting the application, verify all required environment variables are set:

```bash
# Check individual variables
echo "Chat Enabled: $RETAIL_UI_CHAT_ENABLED"
echo "Provider: $RETAIL_UI_CHAT_PROVIDER"
echo "Model: $RETAIL_UI_CHAT_MODEL"
echo "Base URL: $RETAIL_UI_CHAT_OPENAI_BASE_URL"
echo "API Key (masked): ${RETAIL_UI_CHAT_OPENAI_API_KEY:0:10}..."
```

## Step 2: Test API Connectivity

Before starting the application, test that the API endpoint is reachable and working:

```bash
# Test with curl (replace with your API key)
curl -X POST https://api.ai.kodekloud.com/chat/completions \
  -H "Authorization: Bearer sk-YOUR-API-KEY-HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek/deepseek-v3.2",
    "messages": [
      {
        "role": "user",
        "content": "Say hello"
      }
    ],
    "max_tokens": 300,
    "temperature": 0.6
  }'
```

Expected response should contain a `choices` array with a message.

## Step 3: Start the Application

### Option A: Using Environment Variables

```bash
# Set all required environment variables
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE
export RETAIL_UI_CHAT_TEMPERATURE=0.6
export RETAIL_UI_CHAT_MAX_TOKENS=300

# Start the application
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
./mvnw spring-boot:run
```

### Option B: Using Command Line Arguments

```bash
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="\
  --retail.ui.chat.enabled=true \
  --retail.ui.chat.provider=openai \
  --retail.ui.chat.model=deepseek/deepseek-v3.2 \
  --retail.ui.chat.openai.base-url=https://api.ai.kodekloud.com \
  --retail.ui.chat.openai.api-key=sk-YOUR-API-KEY-HERE \
  --retail.ui.chat.temperature=0.6 \
  --retail.ui.chat.max-tokens=300"
```

## Step 4: Verify Application Started Successfully

Look for these log messages indicating successful initialization:

```
Creating OpenAI chat client with baseUrl: https://api.ai.kodekloud.com
Using custom OpenAI base URL: https://api.ai.kodekloud.com
```

## Step 5: Test Chat in Browser

1. Open your browser and navigate to `http://localhost:8080`

2. Look for the chat widget/interface in the UI

3. Send a test message (e.g., "Hello, what products do you have?")

4. Verify you receive a response from the AI model

## Step 6: Monitor Application Logs

Watch the application logs for any errors:

```bash
# If you're running in a separate terminal, tail the logs
# The logs should show chat requests and responses
```

Common log indicators:

- **Success**: Chat message processed and response returned
- **Error**: Check error messages in logs for API connectivity issues
- **Timeout**: Check if the API endpoint is reachable and responding

## Testing Different Scenarios

### Test 1: Simple Query

**Message**: "What is your store about?"

**Expected**: Should return information about the demo store based on the system prompt.

### Test 2: Product Question

**Message**: "Do you have any invisible ink pens?"

**Expected**: Should respond about the product based on the system prompt persona.

### Test 3: Multiple Turns

Send multiple messages in sequence to test conversation continuity.

**Note**: Each message is independent in this implementation (no conversation history).

## Troubleshooting

### Chat Widget Not Visible

1. Check if `RETAIL_UI_CHAT_ENABLED=true`
2. Check browser console for JavaScript errors
3. Verify the application started without errors

### Chat Returns Error

1. Check the application logs for detailed error messages
2. Verify API key is valid
3. Test API endpoint directly with curl (see Step 2)
4. Verify base URL is correct (should end with `/v1`)

### API Authentication Failed

1. Verify API key: `echo $RETAIL_UI_CHAT_OPENAI_API_KEY`
2. Check if API key has expired
3. Regenerate API key if necessary

### Timeout Issues

1. Check network connectivity: `ping api.ai.kodekloud.com`
2. Check firewall rules
3. Verify base URL doesn't have typos
4. Try with `curl` to isolate network issues

## Testing with Different Models

If your custom endpoint supports multiple models, test with different ones:

```bash
export RETAIL_UI_CHAT_MODEL=claude-opus-4-7
# restart application
```

## Performance Testing

Monitor response times:

1. Send a message and note the time
2. Check application logs for processing time
3. Verify response time is reasonable for your API

## Clean Up

When done testing:

1. Stop the application (Ctrl+C)
2. Unset environment variables if needed:
   ```bash
   unset RETAIL_UI_CHAT_ENABLED
   unset RETAIL_UI_CHAT_OPENAI_API_KEY
   # etc.
   ```

## Additional Resources

- See `SETUP_CUSTOM_API.md` for detailed configuration options
- See `README.md` for general application setup
- Check Spring AI documentation for more advanced configurations
