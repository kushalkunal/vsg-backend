#!/usr/bin/env bash
# ─────────────────────────────────────────────
#  build-push.sh  —  Build & push to Docker Hub
#  Usage:  ./build-push.sh
# ─────────────────────────────────────────────

# ★ Set your Docker Hub username here
DOCKER_USERNAME="your_dockerhub_username"

# ─── nothing to change below this line ───────
DIR="$(cd "$(dirname "$0")" && pwd)"

# ── Version from pom.xml ──────────────────────
VERSION=$(grep -m1 '<version>' "${DIR}/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d '[:space:]')

# ── Date-based build number (YYYYMMDD.N, resets to 1 each new day) ───
TODAY=$(date +%Y%m%d)
BUILD_FILE="${DIR}/.build-number"
if [ -f "$BUILD_FILE" ]; then
  STORED=$(cat "$BUILD_FILE")          # format: YYYYMMDD:N
  STORED_DATE="${STORED%%:*}"
  STORED_NUM="${STORED##*:}"
  if [ "$STORED_DATE" = "$TODAY" ]; then
    BUILD_SEQ=$(( STORED_NUM + 1 ))
  else
    BUILD_SEQ=1
  fi
else
  BUILD_SEQ=1
fi
echo "${TODAY}:${BUILD_SEQ}" > "$BUILD_FILE"
BUILD_NUM="${TODAY}.${BUILD_SEQ}"

IMAGE="${DOCKER_USERNAME}/vsg-backend"
TAG_LATEST="${IMAGE}:latest"
TAG_VERSION="${IMAGE}:${VERSION}"
TAG_BUILD="${IMAGE}:${VERSION}-build.${BUILD_NUM}"

set -e

echo "============================================"
echo "  Image   : ${IMAGE}"
echo "  Version : ${VERSION}"
echo "  Build # : ${BUILD_NUM}"
echo "  Tags    :"
echo "    ${TAG_LATEST}"
echo "    ${TAG_VERSION}"
echo "    ${TAG_BUILD}"
echo "============================================"

echo ""
echo "==> Logging in to Docker Hub…"
docker login

echo ""
echo "==> Building JAR…"
mvn -f "${DIR}/pom.xml" clean package -DskipTests -q

echo ""
echo "==> Building Docker image…"
docker build \
  -t "${TAG_LATEST}" \
  -t "${TAG_VERSION}" \
  -t "${TAG_BUILD}" \
  "${DIR}"

echo ""
echo "==> Pushing to Docker Hub…"
docker push "${TAG_LATEST}"
docker push "${TAG_VERSION}"
docker push "${TAG_BUILD}"

echo ""
echo "✓ Done! Pushed:"
echo "    ${TAG_LATEST}"
echo "    ${TAG_VERSION}"
echo "    ${TAG_BUILD}"

