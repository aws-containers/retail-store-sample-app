# Quick Start - Enable Chat with Custom API

Get the chat feature working with your custom OpenAI-compatible API in 5 minutes.

## TL;DR

```bash
cd /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui

# Set environment variables
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE

# Start application
./mvnw spring-boot:run

# Visit http://localhost:8080 and use the chat widget
```

## Configuration Mapping

The environment variables map to Spring properties like this:

| Environment Variable | Spring Property |
|----------------------|-----------------|
| `RETAIL_UI_CHAT_ENABLED` | `retail.ui.chat.enabled` |
| `RETAIL_UI_CHAT_PROVIDER` | `retail.ui.chat.provider` |
| `RETAIL_UI_CHAT_MODEL` | `retail.ui.chat.model` |
| `RETAIL_UI_CHAT_TEMPERATURE` | `retail.ui.chat.temperature` |
| `RETAIL_UI_CHAT_MAX_TOKENS` | `retail.ui.chat.max-tokens` |
| `RETAIL_UI_CHAT_OPENAI_BASE_URL` | `retail.ui.chat.openai.base-url` |
| `RETAIL_UI_CHAT_OPENAI_API_KEY` | `retail.ui.chat.openai.api-key` |

## What Each Variable Does

- **RETAIL_UI_CHAT_ENABLED**: Turns on/off the chat feature (required: `true`)
- **RETAIL_UI_CHAT_PROVIDER**: Which AI service to use (required: `openai`, `bedrock`, or `mock`)
- **RETAIL_UI_CHAT_MODEL**: Model name at your endpoint (required: `deepseek/deepseek-v3.2`)
- **RETAIL_UI_CHAT_OPENAI_BASE_URL**: Your custom API endpoint URL (required for openai: `https://api.ai.kodekloud.com`)
- **RETAIL_UI_CHAT_OPENAI_API_KEY**: Your API authentication key (required for openai: `sk-...`)
- **RETAIL_UI_CHAT_TEMPERATURE**: Model creativity (optional: 0-1, default: 0.6)
- **RETAIL_UI_CHAT_MAX_TOKENS**: Max response length (optional: default: 300)

## Verification Checklist

After starting the app, verify:

- [ ] Application started without errors
- [ ] You see log: "Creating OpenAI chat client with baseUrl: https://api.ai.kodekloud.com"
- [ ] Browser shows UI at http://localhost:8080
- [ ] Chat widget is visible in the UI
- [ ] You can send a message and receive a response

## If It Doesn't Work

1. **Check logs for errors**:
   - Look for "Creating OpenAI chat client" message
   - Check for connection errors

2. **Verify API connectivity**:
   ```bash
   curl -X POST https://api.ai.kodekloud.com/chat/completions \
     -H "Authorization: Bearer sk-YOUR-API-KEY-HERE" \
     -H "Content-Type: application/json" \
     -d '{"model":"deepseek/deepseek-v3.2","messages":[{"role":"user","content":"test"}],"max_tokens":10}'
   ```

3. **Check environment variables**:
   ```bash
   echo $RETAIL_UI_CHAT_ENABLED
   echo $RETAIL_UI_CHAT_OPENAI_API_KEY
   ```

## Using .env File (Alternative)

Instead of exporting variables, create `.env` file:

```bash
# Create .env in /Users/abhay/Desktop/Amazon/retail-store-sample-app/src/ui/
cat > .env << 'EOF'
RETAIL_UI_CHAT_ENABLED=true
RETAIL_UI_CHAT_PROVIDER=openai
RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY-HERE
EOF

# Then source it
source .env
./mvnw spring-boot:run
```

## For Docker

If running with Docker Compose, just put the `.env` file in the UI directory and run:

```bash
docker compose up
```

Docker Compose automatically loads the `.env` file.

## Next Steps

- See `SETUP_CUSTOM_API.md` for detailed configuration options
- See `TESTING_CHAT.md` for comprehensive testing guide
- See `README.md` for general application setup

---

**Questions?** Check the logs first - they usually tell you exactly what's wrong!
