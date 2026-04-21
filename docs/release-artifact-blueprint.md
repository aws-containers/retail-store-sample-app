# Release Artifact Blueprint

## Manifest Definitions

This repository uses two different manifest concepts for different purposes:

1. Docker manifest list
- Purpose: Aggregate multi-architecture images (amd64 and arm64) under one image tag so clients can pull a single tag and get the correct architecture.
- Producer: scripts/create-docker-manifest.sh
- Consumer: Docker runtime/image pull clients.
- Scope: Container image publication.

2. Release manifest YAML
- Purpose: Immutable release record for audit and handoff, including per-service image digests by AWS region.
- Producer: scripts/generate-release-manifest.sh (triggered by .github/workflows/release-artifact.yml)
- Consumer: Release governance, audit workflows, and cross-repository automation.
- Scope: Release traceability artifact.

## Artifact vs Deployment Source of Truth

| Item | Role | Source | Used by | Notes |
| --- | --- | --- | --- | --- |
| Docker manifest list | Distribution artifact | ECR image manifest list | Runtime image pulls | One tag maps to amd64/arm64 image variants. |
| Release manifest YAML | Release artifact | dist/release/release-manifest-<release_id>.yaml | Audit/handoff/automation | Stores per-region digest evidence for a release. |
| Version set JSON | Deployment source of truth | failover repo deploy/versions/<env>.json | failover workflows (deploy-env.yml) | Actual deployment reads this file and validates it against deploy/schema/version-set.schema.json. |

## End-to-End Bridge to Failover Version Set

After generating the release manifest artifact, .github/workflows/release-artifact.yml dispatches one repository_dispatch event per service to the failover repo workflow Upsert Version Set.

Each event includes:
- service_name
- image_repository
- image_digest
- release_id
- app_commit_sha
- environment
- chart and deployment metadata from FAILOVER_SERVICE_CONFIG_JSON

This enables the failover repo to:
1. Open/update version-set PR automatically.
2. Enforce promotion policy via promotion-guard.yml.
3. Trigger deployment via deploy-env.yml after merge.

## Required Repository Configuration

Set the following in the app repo:
- Secret FAILOVER_DISPATCH_REPO (format: owner/repo)
- Secret FAILOVER_DISPATCH_TOKEN (token with permission to dispatch events on failover repo)
- Variable FAILOVER_SERVICE_CONFIG_JSON (object keyed by service name with release, namespace, chart, chart_version, optional smoke_deployment and values_files)
- Optional variable FAILOVER_DIGEST_REGION (preferred region when selecting digest from release manifest)
