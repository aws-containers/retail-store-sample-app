#!/bin/bash

set -e

url=$1

if [ -z "$url" ]; then
  echo "Error: No URL specified"
  exit 1
fi

status_code=$(curl -o /dev/null -s -w "%{http_code}\n" $url || true)

if [[ "$status_code" -ne 200 ]] ; then
  echo "Error: HTTP status code not 200 ($status_code) from $url"
  exit 1
fi

echo "Success: HTTP status code $status_code from $url"