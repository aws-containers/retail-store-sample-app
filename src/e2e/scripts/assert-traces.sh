#!/usr/bin/env bash

set -euo pipefail

JAEGER_API="${JAEGER_API:-http://localhost:16686/api}"
LOOKBACK="${LOOKBACK:-10m}"
PASS=0
FAIL=0

assert() {
  local description="$1"
  local query="$2"
  local traces="$3"

  if echo "$traces" | jq -e "$query" > /dev/null 2>&1; then
    echo "  ✔ $description"
    PASS=$((PASS+1))
  else
    echo "  ✘ $description"
    FAIL=$((FAIL+1))
  fi
}

fetch_traces() {
  local service="$1"
  local operation="$2"
  curl -sf "$JAEGER_API/traces?service=$service&operation=$(python3 -c "import urllib.parse; print(urllib.parse.quote('$operation'))")&lookback=$LOOKBACK&limit=5"
}

echo ""
echo "==> Catalog page trace assertions"
TRACES=$(fetch_traces "ui" "GET /catalog")

assert "UI span exists for GET /catalog" \
  '.data[0].spans[] | select(.operationName == "GET /catalog")' \
  "$TRACES"

assert "Catalog service span exists showing trace propagation" \
  '.data[0].spans[] | select(.operationName == "/catalog/products")' \
  "$TRACES"

assert "Catalog SQL query was traced" \
  '.data[0].spans[] | select(.tags[] | select(.key == "db.statement" and (.value | startswith("SELECT * FROM `products`"))))' \
  "$TRACES"

assert "Cart service span exists showing trace propagation" \
  '.data[0].spans[] | select(.operationName == "GET /carts/{customerId}")' \
  "$TRACES"

assert "DynamoDB cart query was traced" \
  '.data[0].spans[] | select(.tags[] | select(.key == "aws.table.name" and .value == "Items"))' \
  "$TRACES"

echo ""
echo "==> Checkout page trace assertions"
TRACES=$(fetch_traces "ui" "GET /checkout")

assert "UI span exists for GET /checkout" \
  '.data[0].spans[] | select(.operationName == "GET /checkout")' \
  "$TRACES"

assert "Checkout service span exists showing trace propagation" \
  '.data[0].spans[] | select(.operationName == "POST /checkout/:customerId/update")' \
  "$TRACES"

assert "Redis set span exists showing checkout session was persisted" \
  '.data[0].spans[] | select(.operationName == "set" and (.tags[] | select(.key == "db.system" and .value == "redis")))' \
  "$TRACES"

echo ""
if [ "$FAIL" -gt 0 ]; then
  echo "FAILED: $FAIL assertion(s) failed, $PASS passed"
  exit 1
else
  echo "PASSED: all $PASS assertions passed"
fi
