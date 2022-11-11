#!/bin/bash

set -e

SCRIPT_DIR=$(dirname "$0")

rm -rf $SCRIPT_DIR/../src/main/java/com/amazon/sample/events/orders/*.java

mkdir -p $SCRIPT_DIR/../src/main/java/com/amazon/sample/events/orders

jsonschema2pojo --use-title-as-classname --annotation-style JACKSON2 --long-integers \
  -t $SCRIPT_DIR/../src/main/java -p com.amazon.sample.events.orders \
  -s $SCRIPT_DIR/../events/order-created-event.schema.json \
  -s $SCRIPT_DIR/../events/order-cancelled-event.schema.json