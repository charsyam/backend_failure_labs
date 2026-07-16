#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
COUNT="${COUNT:-1000}"

echo "Creating $COUNT posts with a single HTTP request ..."

curl -fsS -X POST "$BASE_URL/posts/bulk?count=$COUNT"

echo
