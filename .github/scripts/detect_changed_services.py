#!/usr/bin/env python3
"""Detect changed retail services and emit a GitHub Actions matrix."""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from pathlib import Path


SERVICES = {
    "cart": {
        "context": "src/cart",
        "dockerfile": "src/cart/Dockerfile",
    },
    "catalog": {
        "context": "src/catalog",
        "dockerfile": "src/catalog/Dockerfile",
    },
    "checkout": {
        "context": "src/checkout",
        "dockerfile": "src/checkout/Dockerfile",
    },
    "orders": {
        "context": "src/orders",
        "dockerfile": "src/orders/Dockerfile",
    },
    "ui": {
        "context": "src/ui",
        "dockerfile": "src/ui/Dockerfile",
    },
}

# Changing the CI workflow or the detector itself affects all services.
GLOBAL_BUILD_PATHS = {
    ".github/workflows/ci.yml",
    ".github/scripts/detect_changed_services.py",
}

ZERO_SHA = "0" * 40


def run_git(*args: str) -> str:
    """Run a Git command and return stdout."""

    result = subprocess.run(
        ["git", *args],
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    if result.returncode != 0:
        print(result.stderr, file=sys.stderr)
        raise RuntimeError(
            f"git {' '.join(args)} failed "
            f"with exit code {result.returncode}"
        )

    return result.stdout.strip()


def create_empty_tree() -> str:
    """Create an empty Git tree for repositories with one commit."""

    result = subprocess.run(
        ["git", "mktree"],
        input="",
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    if result.returncode != 0:
        raise RuntimeError(
            f"git mktree failed: {result.stderr.strip()}"
        )

    return result.stdout.strip()


def normalize_base(base_sha: str, head_sha: str) -> str:
    """Handle new branches where GitHub provides an all-zero base SHA."""

    if base_sha and base_sha != ZERO_SHA:
        return base_sha

    parent_result = subprocess.run(
        ["git", "rev-parse", f"{head_sha}^"],
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
    )

    if parent_result.returncode == 0:
        return parent_result.stdout.strip()

    return create_empty_tree()


def get_changed_files(
    event_name: str,
    base_sha: str,
    head_sha: str,
) -> list[str]:
    """Return files changed between the base and head commits."""

    normalized_base = normalize_base(base_sha, head_sha)

    if event_name == "pull_request":
        diff_range = f"{normalized_base}...{head_sha}"
    else:
        diff_range = f"{normalized_base}..{head_sha}"

    output = run_git(
        "diff",
        "--name-only",
        "--diff-filter=ACMRD",
        diff_range,
    )

    return [
        line.strip().replace("\\", "/")
        for line in output.splitlines()
        if line.strip()
    ]


def select_services(files: list[str]) -> list[str]:
    """Map changed files to retail services."""

    if GLOBAL_BUILD_PATHS.intersection(files):
        return list(SERVICES)

    selected: list[str] = []

    for service in SERVICES:
        prefix = f"src/{service}/"

        if any(path.startswith(prefix) for path in files):
            selected.append(service)

    return selected


def write_github_output(name: str, value: str) -> None:
    """Write a step output when running inside GitHub Actions."""

    output_file = os.getenv("GITHUB_OUTPUT")

    if not output_file:
        return

    with Path(output_file).open(
        "a",
        encoding="utf-8",
    ) as file:
        file.write(f"{name}={value}\n")


def main() -> int:
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--event",
        required=True,
        choices=("pull_request", "push"),
    )

    parser.add_argument("--base", required=True)
    parser.add_argument("--head", required=True)

    args = parser.parse_args()

    files = get_changed_files(
        event_name=args.event,
        base_sha=args.base,
        head_sha=args.head,
    )

    selected_services = select_services(files)

    matrix = {
        "include": [
            {
                "service": service,
                **SERVICES[service],
            }
            for service in selected_services
        ]
    }

    matrix_json = json.dumps(
        matrix,
        separators=(",", ":"),
    )

    print("Changed files:")

    if files:
        for path in files:
            print(f"  - {path}")
    else:
        print("  - none")

    selected_text = (
        ", ".join(selected_services)
        if selected_services
        else "none"
    )

    print(f"Selected services: {selected_text}")
    print(f"Matrix: {matrix_json}")

    write_github_output(
        "has_changes",
        "true" if selected_services else "false",
    )

    write_github_output(
        "services",
        ",".join(selected_services),
    )

    write_github_output(
        "matrix",
        matrix_json,
    )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())