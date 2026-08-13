#!/usr/bin/env bash
set -euo pipefail

ITEM_BASE_URL="${ITEM_BASE_URL:-http://localhost:8080}"
SEARCH_BASE_URL="${SEARCH_BASE_URL:-http://localhost:8081}"
COUNT="${COUNT:-500}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-30}"
POLL_INTERVAL="${POLL_INTERVAL:-0.2}"

if ! [[ "$COUNT" =~ ^[1-9][0-9]*$ ]]; then
  echo "COUNT must be a positive integer" >&2
  exit 2
fi

if ! [[ "$TIMEOUT_SECONDS" =~ ^[1-9][0-9]*$ ]]; then
  echo "TIMEOUT_SECONDS must be a positive integer" >&2
  exit 2
fi

for command in curl jq; do
  if ! command -v "$command" >/dev/null 2>&1; then
    echo "Required command not found: $command" >&2
    exit 2
  fi
done

work_dir="$(mktemp -d "${TMPDIR:-/tmp}/lab4-reproduce.XXXXXX")"
created_ids_file="$work_dir/created-ids.json"
results_file="$work_dir/results.json"
trap 'rm -rf "$work_dir"' EXIT

printf '[]\n' > "$created_ids_file"
curl -fsS -X DELETE "$SEARCH_BASE_URL/indexes/results" >/dev/null

echo "Creating $COUNT items ..."
for ((i = 1; i <= COUNT; i++)); do
  response="$(curl -fsS -X POST "$ITEM_BASE_URL/items?name=lab4-item-$i")"
  item_id="$(jq -er '.id' <<<"$response")"
  jq --argjson item_id "$item_id" '. + [$item_id]' "$created_ids_file" > "$created_ids_file.next"
  mv "$created_ids_file.next" "$created_ids_file"

  if ((i % 100 == 0)); then
    echo "  created=$i"
  fi
done

deadline=$((SECONDS + TIMEOUT_SECONDS))
processed=0
while ((SECONDS < deadline)); do
  curl -fsS "$SEARCH_BASE_URL/indexes/results" > "$results_file"
  processed="$(jq --slurpfile ids "$created_ids_file" '[.[] | select(.itemId as $id | $ids[0] | index($id))] | length' "$results_file")"
  if ((processed >= COUNT)); then
    break
  fi
  sleep "$POLL_INTERVAL"
done

success="$(jq --slurpfile ids "$created_ids_file" '[.[] | select(.itemId as $id | $ids[0] | index($id)) | select(.success)] | length' "$results_file")"
failure="$(jq --slurpfile ids "$created_ids_file" '[.[] | select(.itemId as $id | $ids[0] | index($id)) | select(.success | not)] | length' "$results_file")"
pending=$((COUNT - processed))

echo "Result: requested=$COUNT processed=$processed success=$success failure=$failure pending=$pending"

if ((pending > 0)); then
  echo "Timed out before all items were processed" >&2
  exit 2
fi

if ((failure == 0)); then
  echo "The issue was not reproduced. Run the script again."
  exit 1
fi

echo "Issue reproduced. Failed item IDs:"
jq --slurpfile ids "$created_ids_file" '[.[] | select(.itemId as $id | $ids[0] | index($id)) | select(.success | not) | .itemId]' "$results_file"
