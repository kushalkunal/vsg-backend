#!/usr/bin/env bash
# build-push.sh — Clone repo, build Docker image tagged with git commit SHA, push to Docker Hub.
#
# Required env vars:
#   DOCKER_HUB_USERNAME   — Docker Hub account name
#
# Usage:
#   ./build-push.sh              # clone main, build & push :<short-sha> + :latest
#   ./build-push.sh --no-push   # build only, skip push
#   ./build-push.sh --branch develop   # use a specific branch

set -euo pipefail

# ── Config from env ───────────────────────────────────────────────────────────
DOCKER_HUB_USERNAME="${DOCKER_HUB_USERNAME:?DOCKER_HUB_USERNAME env var is required}"
IMAGE="${DOCKER_HUB_USERNAME}/vsg-backend"
REPO_URL="https://github.com/kushalkunal/vsg-backend.git"
BRANCH="main"
PUSH=true
CLONE_DIR=""

# ── Parse arguments ───────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-push)   PUSH=false ; shift ;;
    --branch)    BRANCH="$2" ; shift 2 ;;
    *) echo "Unknown argument: $1" ; exit 1 ;;
  esac
done

# ── Cleanup on exit ───────────────────────────────────────────────────────────
cleanup() {
  if [[ -n "$CLONE_DIR" && -d "$CLONE_DIR" ]]; then
    echo "▶ Cleaning up clone at $CLONE_DIR"
    rm -rf "$CLONE_DIR"
  fi
}
trap cleanup EXIT

# ── Clone the repo ────────────────────────────────────────────────────────────
CLONE_DIR="$(mktemp -d)"
echo "▶ Cloning $REPO_URL (branch: $BRANCH) into $CLONE_DIR"
git clone --depth=1 --branch "$BRANCH" "$REPO_URL" "$CLONE_DIR"

# ── Derive image tag from commit SHA ─────────────────────────────────────────
COMMIT_SHA="$(git -C "$CLONE_DIR" rev-parse --short HEAD)"
COMMIT_MSG="$(git -C "$CLONE_DIR" log -1 --pretty=format:'%s')"
echo "▶ Commit: $COMMIT_SHA — $COMMIT_MSG"

# ── Build ─────────────────────────────────────────────────────────────────────
echo "▶ Building $IMAGE:$COMMIT_SHA"
docker build \
  --platform linux/amd64,linux/arm64 \
  --tag "$IMAGE:$COMMIT_SHA" \
  --tag "$IMAGE:latest" \
  --label "org.opencontainers.image.revision=$COMMIT_SHA" \
  --label "org.opencontainers.image.source=$REPO_URL" \
  --label "org.opencontainers.image.created=$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
  "$CLONE_DIR"

# ── Push ──────────────────────────────────────────────────────────────────────
if $PUSH; then
  echo "▶ Pushing $IMAGE:$COMMIT_SHA"
  docker push "$IMAGE:$COMMIT_SHA"
  echo "▶ Pushing $IMAGE:latest"
  docker push "$IMAGE:latest"
  echo "✔ Done — pushed $IMAGE:$COMMIT_SHA and $IMAGE:latest"

  # ── Remove local images after push to free disk space ────────────────────
  echo "▶ Removing local images"
  docker rmi "$IMAGE:$COMMIT_SHA" "$IMAGE:latest" || true
  docker image prune -f
  echo "✔ Local images removed"

  # ── Remove the cloned repo ────────────────────────────────────────────────
  echo "▶ Removing cloned repo at $CLONE_DIR"
  rm -rf "$CLONE_DIR"
  CLONE_DIR=""   # prevent trap from double-removing
  echo "✔ Cloned repo removed"
else
  echo "✔ Done — built $IMAGE:$COMMIT_SHA (push skipped)"
fi
