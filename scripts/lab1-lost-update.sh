#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
ROUNDS="${ROUNDS:-100}"

json_number() {
  local json="$1"
  local key="$2"
  printf '%s' "$json" | sed -nE "s/.*\"$key\":([0-9]+).*/\\1/p"
}

reset_json="$(curl -sS -X POST "$BASE_URL/labs/1-lost-update/reset")"
user_id="$(json_number "$reset_json" "id")"

if [[ -z "$user_id" ]]; then
  echo "reset failed: $reset_json"
  exit 1
fi

for round in $(seq 1 "$ROUNDS"); do
  likes_out="$(mktemp)"
  comments_out="$(mktemp)"

  curl -sS -X POST "$BASE_URL/labs/1-lost-update/users/$user_id/unsafe-likes?delta=1" > "$likes_out" &
  likes_pid=$!

  curl -sS -X POST "$BASE_URL/labs/1-lost-update/users/$user_id/unsafe-comments?delta=1" > "$comments_out" &
  comments_pid=$!

  wait "$likes_pid"
  wait "$comments_pid"
  echo "round=$round userId=$user_id"

  rm -f "$likes_out" "$comments_out"
done

sleep 1

final_json="$(curl -sS "$BASE_URL/labs/1-lost-update/users/$user_id")"
final_likes="$(json_number "$final_json" "likes")"
final_comments="$(json_number "$final_json" "comments")"

expected_likes="$ROUNDS"
expected_comments="$ROUNDS"

echo "final: $final_json"

if [[ "$final_likes" != "$expected_likes" || "$final_comments" != "$expected_comments" ]]; then
  echo "LOST_UPDATE_REPRODUCED expected likes=$expected_likes comments=$expected_comments, actual likes=$final_likes comments=$final_comments"
  exit 0
fi

echo "LOST_UPDATE_NOT_REPRODUCED rounds=$ROUNDS"
exit 1
