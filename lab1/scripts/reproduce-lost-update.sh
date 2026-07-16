#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
ROUNDS="${ROUNDS:-1000}"
CONCURRENCY="${CONCURRENCY:-50}"

post_json="$(curl -fsS -X POST "$BASE_URL/posts/reset")"
post_id="$(printf '%s' "$post_json" | sed -nE 's/.*"id":([0-9]+).*/\1/p')"

if [[ -z "$post_id" ]]; then
  echo "Failed to create a post: $post_json"
  exit 1
fi

echo "Initial post: $post_json"
echo "Sending $ROUNDS likes and $ROUNDS comments with concurrency=$CONCURRENCY ..."

seq "$ROUNDS" | xargs -P "$CONCURRENCY" -I '{}' sh -c '
  curl -fsS -X POST "$1/posts/$2/likes" >/dev/null &
  curl -fsS -X POST "$1/posts/$2/comments" >/dev/null &
  wait
' _ "$BASE_URL" "$post_id"

final_json="$(curl -fsS "$BASE_URL/posts/$post_id")"
final_likes="$(printf '%s' "$final_json" | sed -nE 's/.*"likes":([0-9]+).*/\1/p')"
final_comments="$(printf '%s' "$final_json" | sed -nE 's/.*"comments":([0-9]+).*/\1/p')"
echo "Final post:   $final_json"
echo "Expected:     likes=$ROUNDS comments=$ROUNDS"

if [[ "$final_likes" == "$ROUNDS" && "$final_comments" == "$ROUNDS" ]]; then
  echo "Lost Update was not reproduced. Run the script again."
  exit 1
fi

echo "Lost Update reproduced: actual likes=$final_likes comments=$final_comments"
