#!/bin/bash

set -e

file_path=".random_password"

if [ ! -f "$file_path" ]; then
  echo $RANDOM | md5sum | head -c 20 > $file_path
fi

cat $file_path