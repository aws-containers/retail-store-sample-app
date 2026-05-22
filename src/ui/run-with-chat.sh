#!/bin/bash

# Run the Spring Boot app with chat feature enabled
# Replace sk-YOUR-API-KEY-HERE with your actual API key

# STEP 1: Set all environment variables
export RETAIL_UI_CHAT_ENABLED=true
export RETAIL_UI_CHAT_PROVIDER=openai
export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2
export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com
export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-VoS0usxVHeOrnJB6XpCk1w
export RETAIL_UI_CHAT_TEMPERATURE=0.6
export RETAIL_UI_CHAT_MAX_TOKENS=300

# STEP 2: Verify variables are set
echo "=========================================="
echo "Chat Configuration"
echo "=========================================="
echo "✓ CHAT_ENABLED: $RETAIL_UI_CHAT_ENABLED"
echo "✓ PROVIDER: $RETAIL_UI_CHAT_PROVIDER"
echo "✓ MODEL: $RETAIL_UI_CHAT_MODEL"
echo "✓ BASE_URL: $RETAIL_UI_CHAT_OPENAI_BASE_URL"
echo "✓ API_KEY: ${RETAIL_UI_CHAT_OPENAI_API_KEY:0:15}..."
echo "=========================================="
echo ""

# STEP 3: Check if API key was updated
if [[ "$RETAIL_UI_CHAT_OPENAI_API_KEY" == "sk-YOUR-API-KEY-HERE" ]]; then
  echo "⚠️  WARNING: You need to update the API key!"
  echo "Edit this file and replace: sk-YOUR-API-KEY-HERE"
  echo "with your actual API key"
  echo ""
  echo "Then run again."
  exit 1
fi

# STEP 4: Start the application
echo "Starting Spring Boot application with chat enabled..."
echo ""
echo "Once started, visit:"
echo "  → http://localhost:8080"
echo ""
echo "Look for the chat widget in the UI"
echo ""

./mvnw spring-boot:run
