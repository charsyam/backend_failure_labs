#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
REQUESTS="${REQUESTS:-10}"
PAGE="${PAGE:-0}"
SIZE="${SIZE:-20}"

started_at="$(date +%s)"

for request in $(seq 1 "$REQUESTS"); do
  response="$(curl -fsS "$BASE_URL/posts/page?page=$PAGE&size=$SIZE")"
  total_elements="$(printf '%s' "$response" | sed -nE 's/.*"totalElements":([0-9]+).*/\1/p')"
  total_pages="$(printf '%s' "$response" | sed -nE 's/.*"totalPages":([0-9]+).*/\1/p')"
  echo "request=$request totalElements=$total_elements totalPages=$total_pages"
done

finished_at="$(date +%s)"
echo "elapsedSeconds=$((finished_at - started_at)) requests=$REQUESTS"
