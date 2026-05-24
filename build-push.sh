#!/usr/bin/env bash
# build-push.sh — Build and push the VSG backend Docker image
# Usage:
#   ./build-push.sh                  # builds & pushes kushalkunal/vsg-backend:latest
#   ./build-push.sh 1.2.0            # builds & pushes :1.2.0 and :latest
#   ./build-push.sh 1.2.0 --no-push  # build only, skip push

set -euo pipefail

IMAGE="kushalkunal/vsg-backend"
VERSION="${1:-latest}"
PUSH=true
if [[ "${2:-}" == "--no-push" ]]; then PUSH=false; fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "▶ Building $IMAGE:$VERSION"
docker build \
  --platform linux/amd64,linux/arm64 \
  --tag "$IMAGE:$VERSION" \
  $([ "$VERSION" != "latest" ] && echo "--tag $IMAGE:latest") \
  "$SCRIPT_DIR"

if $PUSH; then
  echo "▶ Pushing $IMAGE:$VERSION"
  docker push "$IMAGE:$VERSION"
  if [ "$VERSION" != "latest" ]; then
    echo "▶ Pushing $IMAGE:latest"
    docker push "$IMAGE:latest"
  fi
  echo "✔ Done — $IMAGE:$VERSION pushed"
else
  echo "✔ Done — $IMAGE:$VERSION built (push skipped)"
fi
