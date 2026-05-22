# Configuration Summary

All the pieces you need to understand and configure the chat feature.

## What's Changed

Documentation added to help you configure and debug the chat feature with custom OpenAI-compatible APIs:

1. **README.md** - Updated with detailed chat configuration section
2. **QUICK_START.md** - Fast track to get running in 5 minutes
3. **SETUP_CUSTOM_API.md** - Comprehensive configuration guide
4. **TESTING_CHAT.md** - Step-by-step testing procedures
5. **JAVA_IMPLEMENTATION.md** - Deep dive into how the code works
6. **CONFIG_SUMMARY.md** - This file

## Files You Modified

The following configuration files were already in place and are working correctly:

```
src/main/resources/application.yml
├─ retail.ui.chat configuration section
└─ Supports all environment variables

src/main/java/com/amazon/sample/ui/config/chat/
├─ ChatProperties.java (loads general config)
├─ OpenAIChatProperties.java (loads OpenAI-specific config)
├─ OpenAIChatConfig.java ✅ ALREADY CONFIGURED FOR CUSTOM BASE URLs
├─ BedrockChatConfig.java (for AWS Bedrock)
└─ MockChatConfig.java (for testing)

src/main/java/com/amazon/sample/ui/web/
└─ ChatController.java (REST endpoint)
```

**Note**: No Java code changes were needed! The implementation already supports custom OpenAI-compatible APIs.

## How Environment Variables Flow

```
Step 1: Set Environment Variables
RETAIL_UI_CHAT_ENABLED=true
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-...

        ↓

Step 2: Spring Boot Bootstrap
Loads environment variables automatically

        ↓

Step 3: ChatProperties Bean Created
Reads: enabled, provider, model, temperature, max-tokens
Source: "retail.ui.chat.*" properties

        ↓

Step 4: OpenAIChatProperties Bean Created
Reads: baseUrl, apiKey
Source: "retail.ui.chat.openai.*" properties

        ↓

Step 5: OpenAIChatConfig Evaluated
Condition: provider == "openai" ✓ (true)
Creates: ChatClient bean

        ↓

Step 6: ChatClient Built
- Custom base URL: https://api.ai.kodekloud.com
- API key: sk-...
- Model: deepseek/deepseek-v3.2

        ↓

Step 7: ChatController Gets ChatClient
Autowires: @Autowired private ChatClient client;

        ↓

Step 8: Frontend Uses /chat/submit
Sends messages → ChatClient calls custom API → Gets responses
```

## Configuration Options

### Required for Custom API

| Variable | Value | Example |
|----------|-------|---------|
| `RETAIL_UI_CHAT_ENABLED` | `true` | `true` |
| `RETAIL_UI_CHAT_PROVIDER` | `openai` | `openai` |
| `RETAIL_UI_CHAT_MODEL` | Model name | `deepseek/deepseek-v3.2` |
| `RETAIL_UI_CHAT_OPENAI_BASE_URL` | API endpoint | `https://api.ai.kodekloud.com` |
| `RETAIL_UI_CHAT_OPENAI_API_KEY` | API key | `sk-...` |

### Optional

| Variable | Default | Notes |
|----------|---------|-------|
| `RETAIL_UI_CHAT_TEMPERATURE` | `0.6` | Range: 0-1 (higher = more creative) |
| `RETAIL_UI_CHAT_MAX_TOKENS` | `300` | Maximum response length |

## How to Set Variables

### Option 1: Inline (Recommended for quick tests)

```bash
RETAIL_UI_CHAT_ENABLED=true \
RETAIL_UI_CHAT_PROVIDER=openai \
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2 \
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com \
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY \
./mvnw spring-boot:run
```

### Option 2: Export Variables

```bash
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
# ... set all variables
./mvnw spring-boot:run
```

### Option 3: .env File (Recommended for consistency)

Create `.env` in the UI directory:

```env
RETAIL_UI_CHAT_ENABLED=true
RETAIL_UI_CHAT_PROVIDER=openai
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY
```

Then source and run:

```bash
source .env
./mvnw spring-boot:run
```

### Option 4: Spring Properties File

Create `src/main/resources/application-local.yml`:

```yaml
retail:
  ui:
    chat:
      enabled: true
      provider: openai
      model: deepseek/deepseek-v3.2
      openai:
        base-url: https://api.ai.kodekloud.com
        api-key: sk-YOUR-API-KEY
```

Run with:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

## Validation Checklist

Before starting the application, verify:

- [ ] API key is valid and not expired
- [ ] Base URL is correct (usually ends with `/v1`)
- [ ] Model name exists at the endpoint
- [ ] Network access to the API endpoint is available
- [ ] All required environment variables are set

After starting the application, verify:

- [ ] Application started without errors
- [ ] Logs show "Creating OpenAI chat client with baseUrl: ..."
- [ ] UI loads at http://localhost:8080
- [ ] Chat widget is visible in the UI
- [ ] First chat message works

## Common Problems & Solutions

| Problem | Cause | Solution |
|---------|-------|----------|
| Chat widget not visible | Chat not enabled | Check `RETAIL_UI_CHAT_ENABLED=true` |
| "Cannot connect to API" | Wrong base URL | Verify `RETAIL_UI_CHAT_OPENAI_BASE_URL` |
| "Unauthorized" or 401 | Invalid API key | Check `RETAIL_UI_CHAT_OPENAI_API_KEY` |
| "Model not found" or 404 | Wrong model name | Verify model exists at your endpoint |
| No response from chat | Timeout or network issue | Test with curl first (see TESTING_CHAT.md) |
| Configuration not loading | Wrong property prefix | Use exact variable names with underscores |

## Testing the Integration

Quick test without UI:

```bash
# 1. Test API directly
curl -X POST https://api.ai.kodekloud.com/chat/completions \
  -H "Authorization: Bearer sk-YOUR-API-KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek/deepseek-v3.2",
    "messages": [{"role": "user", "content": "Hello"}],
    "max_tokens": 300
  }'

# 2. Start app with chat enabled
source .env && ./mvnw spring-boot:run

# 3. Test application endpoint
curl -X POST http://localhost:8080/chat/submit \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, what products do you have?"}'

# 4. Visit UI in browser
# http://localhost:8080
```

## Documentation Map

- **QUICK_START.md** - Get running in 5 minutes
- **SETUP_CUSTOM_API.md** - Detailed configuration guide
- **TESTING_CHAT.md** - Comprehensive testing guide
- **JAVA_IMPLEMENTATION.md** - Understand the code
- **README.md** - General application info
- **CONFIG_SUMMARY.md** - This file

## Support Resources

1. **Spring AI Documentation**: https://docs.spring.io/spring-ai/reference/
2. **OpenAI API Docs**: https://platform.openai.com/docs/
3. **KodeKloud API**: Your provider's documentation
4. **Application Logs**: Most helpful for debugging

## What's Working

✅ Environment variable loading via Spring Boot
✅ Custom base URL support in OpenAiApi
✅ API key configuration
✅ Model parameter configuration
✅ ChatClient dependency injection
✅ REST endpoint for chat submissions
✅ Server-Sent Events streaming

## What to Avoid

❌ Committing `.env` files to version control (add to `.gitignore`)
❌ Logging sensitive API keys (framework handles this)
❌ Hardcoding credentials in source code
❌ Using HTTP for production APIs (use HTTPS)
❌ Mixing environment variable prefixes (use `RETAIL_UI_CHAT_*`)

---

**Next Step**: See `QUICK_START.md` to get running immediately!
