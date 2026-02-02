#!/usr/bin/env bash
set -euo pipefail

# If there's not a GITHUB_OUTPUT, replace output with temp file.
: "${GITHUB_OUTPUT:=/tmp/github_output}"

MOD_VERSION=$(grep '^mod_version=' gradle.properties | cut -d'=' -f2)

CURRENT_TAG="release-$MOD_VERSION"

if git rev-parse "refs/tags/$CURRENT_TAG" >/dev/null 2>&1; then
  SKIP_RELEASE=true
else
  SKIP_RELEASE=false
fi

LAST_TAG=$(git describe --tags --abbrev=0 --match "release-*" 2>/dev/null || echo "")
# cat /tmp/github_output

if [ -n "$LAST_TAG" ] ; then
  if [ "$LAST_TAG" = "$CURRENT_TAG" ]; then
    CHANGELOG="Nothing to show."
  else
    CHANGELOG=$(git log "$LAST_TAG"..HEAD --pretty=format:"- %s")
  fi
else
  CHANGELOG=$(git log --pretty=format:"- %s")
fi

{
  echo "mod_version=$MOD_VERSION"
  echo "skip_release=$SKIP_RELEASE"
  echo "changelog<<EOF"
  echo "$CHANGELOG"
  echo "EOF"
} >> "$GITHUB_OUTPUT"
