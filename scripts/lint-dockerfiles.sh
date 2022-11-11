#!/bin/bash

set -e

hadolint images/java17/Dockerfile -t warning
hadolint images/go/Dockerfile -t warning
hadolint images/nodejs/Dockerfile -t warning

hadolint src/assets/Dockerfile -t warning