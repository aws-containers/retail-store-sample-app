#!/bin/sh
set -eu

section() {
    printf '\nSECTION|%s\n' "$1"
}

section "IDENTITY"

id
printf 'UID|%s\n' "$(id -u)"
printf 'GID|%s\n' "$(id -g)"
printf 'USER|%s\n' "${USER:-unset}"
printf 'WORKDIR|%s\n' "$(pwd)"

section "OPERATING_SYSTEM"

if [ -r /etc/os-release ]; then
    cat /etc/os-release
else
    printf 'INFO|/etc/os-release not found\n'
fi

section "FILESYSTEM"

for path in / /app /tmp; do
    if [ -e "$path" ]; then
        ls -ld "$path"
    fi
done

printf 'ROOT_FILESYSTEM|'

if touch /.runtime-audit-write-test 2>/dev/null; then
    rm -f /.runtime-audit-write-test
    printf 'writable\n'
else
    printf 'not-writable\n'
fi

section "PACKAGE_CACHES"

for path in \
    /var/cache/apk \
    /var/cache/dnf \
    /var/cache/yum \
    /var/lib/apt/lists
do
    if [ -e "$path" ]; then
        printf 'CACHE_PATH|%s\n' "$path"
        ls -la "$path" 2>/dev/null || true
    fi
done

section "TOOLS"

for tool in sh bash curl wget nc java node npm python3; do
    if command -v "$tool" >/dev/null 2>&1; then
        printf '%s|present|%s\n' "$tool" "$(command -v "$tool")"
    else
        printf '%s|absent\n' "$tool"
    fi
done

section "PROCESSES"

ps 2>/dev/null || true
