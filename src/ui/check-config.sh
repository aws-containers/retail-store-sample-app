#!/bin/bash

# Configuration Check Script
# Run this script to verify your chat configuration is correct

echo "========================================"
echo "Chat Configuration Checker"
echo "========================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if a variable is set
check_var() {
  local var_name=$1
  local var_value="${!var_name}"

  if [ -z "$var_value" ]; then
    echo -e "${RED}✗${NC} $var_name: NOT SET"
    return 1
  else
    if [[ "$var_name" == *"API_KEY"* ]]; then
      # Mask API key for security
      local masked="${var_value:0:10}...${var_value: -5}"
      echo -e "${GREEN}✓${NC} $var_name: $masked"
    else
      echo -e "${GREEN}✓${NC} $var_name: $var_value"
    fi
    return 0
  fi
}

# Function to validate URL format
check_url() {
  local url=$1
  if [[ $url =~ ^https?:// ]]; then
    echo -e "${GREEN}✓${NC} Base URL format is valid"
    return 0
  else
    echo -e "${RED}✗${NC} Base URL must start with http:// or https://"
    return 1
  fi
}

# Function to test API connectivity
test_api() {
  local base_url=$1
  local api_key=$2

  echo ""
  echo "Testing API connectivity..."

  if command -v curl &> /dev/null; then
    local response=$(curl -s -w "\n%{http_code}" -X POST "$base_url/v1/chat/completions" \
      -H "Authorization: Bearer $api_key" \
      -H "Content-Type: application/json" \
      -d '{
        "model": "deepseek/deepseek-v3.2",
        "messages": [{"role": "user", "content": "test"}],
        "max_tokens": 10
      }' 2>/dev/null)

    local http_code=$(echo "$response" | tail -n1)

    case $http_code in
      200)
        echo -e "${GREEN}✓${NC} API is reachable and responding (HTTP 200)"
        return 0
        ;;
      401)
        echo -e "${RED}✗${NC} API returned 401 Unauthorized - Check your API key"
        return 1
        ;;
      404)
        echo -e "${RED}✗${NC} API returned 404 Not Found - Check your base URL"
        return 1
        ;;
      *)
        echo -e "${YELLOW}?${NC} API returned HTTP $http_code"
        return 1
        ;;
    esac
  else
    echo -e "${YELLOW}!${NC} curl not found - skipping API connectivity test"
    return 0
  fi
}

# Check required environment variables
echo "Checking environment variables..."
echo ""

required_vars=(
  "RETAIL_UI_CHAT_ENABLED"
  "RETAIL_UI_CHAT_PROVIDER"
  "RETAIL_UI_CHAT_MODEL"
  "RETAIL_UI_CHAT_OPENAI_BASE_URL"
  "RETAIL_UI_CHAT_OPENAI_API_KEY"
)

all_required_set=true
for var in "${required_vars[@]}"; do
  if ! check_var "$var"; then
    all_required_set=false
  fi
done

echo ""
echo "Checking optional environment variables..."
echo ""

optional_vars=(
  "RETAIL_UI_CHAT_TEMPERATURE"
  "RETAIL_UI_CHAT_MAX_TOKENS"
)

for var in "${optional_vars[@]}"; do
  check_var "$var" || true
done

# Validate specific values
echo ""
echo "Validating configuration values..."
echo ""

# Check if chat is enabled
if [ "$RETAIL_UI_CHAT_ENABLED" != "true" ]; then
  echo -e "${YELLOW}!${NC} Chat is not enabled (RETAIL_UI_CHAT_ENABLED != true)"
fi

# Check provider
if [ "$RETAIL_UI_CHAT_PROVIDER" != "openai" ]; then
  echo -e "${YELLOW}!${NC} Provider is '$RETAIL_UI_CHAT_PROVIDER' (expected 'openai')"
fi

# Check URL format
if [ -n "$RETAIL_UI_CHAT_OPENAI_BASE_URL" ]; then
  check_url "$RETAIL_UI_CHAT_OPENAI_BASE_URL" || true
fi

# Test API connectivity if all required vars are set
echo ""
if [ "$all_required_set" = true ]; then
  test_api "$RETAIL_UI_CHAT_OPENAI_BASE_URL" "$RETAIL_UI_CHAT_OPENAI_API_KEY"
else
  echo -e "${YELLOW}!${NC} Skipping API connectivity test - not all variables are set"
fi

# Summary
echo ""
echo "========================================"

if [ "$all_required_set" = true ]; then
  echo -e "${GREEN}✓ All required variables are set${NC}"
  echo ""
  echo "You can now start the application:"
  echo "  ./mvnw spring-boot:run"
  echo ""
  echo "The chat feature should be available at:"
  echo "  http://localhost:8080"
else
  echo -e "${RED}✗ Some required variables are missing${NC}"
  echo ""
  echo "Set the missing variables and try again:"
  echo ""
  echo "  export RETAIL_UI_CHAT_ENABLED=true"
  echo "  export RETAIL_UI_CHAT_PROVIDER=openai"
  echo "  export RETAIL_UI_CHAT_MODEL=deepseek/deepseek-v3.2"
  echo "  export RETAIL_UI_CHAT_OPENAI_BASE_URL=https://api.ai.kodekloud.com"
  echo "  export RETAIL_UI_CHAT_OPENAI_API_KEY=sk-YOUR-API-KEY"
  echo ""
  echo "Or create a .env file with these values and run:"
  echo "  source .env"
fi

echo "========================================"
