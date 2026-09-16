FROM public.ecr.aws/amazonlinux/amazonlinux:2023-minimal

# Apply all available security updates. Amazon Linux 2023 locks the package
# manager to the release snapshot the base image was built from, so
# --releasever=latest is required to pick up newer patched packages.
# https://docs.aws.amazon.com/linux/al2023/ug/deterministic-upgrades-usage.html
RUN microdnf --releasever=latest upgrade -y \
    && \
    microdnf clean all

COPY helpers.js /artillery/helpers.js
COPY scenario.yml /artillery/scenario.yml
