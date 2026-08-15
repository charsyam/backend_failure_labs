#!/usr/bin/env bash
set -euo pipefail

# 고액 주문으로 테이블을 초기화한 뒤 소액 주문을 일정 단위로 추가한다.
# 각 단계에서 검색 API를 반복 호출하고 중앙 응답 시간만 출력한다.

BASE_URL="${BASE_URL:-http://localhost:8080}"
CONTAINER="${MYSQL_CONTAINER:-db-edu-mysql}"
DATABASE="${DB_NAME:-db_edu}"
USER="${DB_USERNAME:-db_edu}"
PASSWORD="${DB_PASSWORD:-db_edu}"
BASE_ROWS="${BASE_ROWS:-1000000}"
BATCH_SIZE="${BATCH_SIZE:-500}"
TOTAL="${TOTAL:-20000}"
AMOUNT="${AMOUNT:-0}"
MEASURE_REPEAT="${MEASURE_REPEAT:-3}"

for value_name in BASE_ROWS BATCH_SIZE TOTAL MEASURE_REPEAT; do
  value="${!value_name}"
  if ! [[ "$value" =~ ^[1-9][0-9]*$ ]]; then
    echo "$value_name must be a positive integer" >&2
    exit 2
  fi
done

if ! [[ "$AMOUNT" =~ ^[0-9]+$ ]]; then
  echo "AMOUNT must be a non-negative integer" >&2
  exit 2
fi

if ((TOTAL % BATCH_SIZE != 0)); then
  echo "TOTAL must be divisible by BATCH_SIZE" >&2
  exit 2
fi

for command in curl docker sort sed; do
  if ! command -v "$command" >/dev/null 2>&1; then
    echo "Required command not found: $command" >&2
    exit 2
  fi
done

if ! curl -fsS "$BASE_URL/orders/search?amountLessThan=500" >/dev/null; then
  echo "Search API is not available: $BASE_URL" >&2
  exit 2
fi

measure_search() {
  local timings=""
  local timing
  local median_line=$(((MEASURE_REPEAT + 1) / 2))

  for ((i = 1; i <= MEASURE_REPEAT; i++)); do
    timing="$(curl -fsS -o /dev/null -w '%{time_total}' \
      "$BASE_URL/orders/search?amountLessThan=500")"
    timings+="$timing"$'\n'
  done

  printf '%s' "$timings" | sort -n | sed -n "${median_line}p"
}

insert_batch() {
  docker exec -i "$CONTAINER" mysql -u"$USER" -p"$PASSWORD" "$DATABASE" 2>/dev/null <<SQL
SET cte_max_recursion_depth = ${BATCH_SIZE};

INSERT INTO orders (customer, amount, note, created_at)
WITH RECURSIVE seq (n) AS (
  SELECT 1
  UNION ALL
  SELECT n + 1 FROM seq WHERE n < ${BATCH_SIZE}
)
SELECT
  CONCAT('cheap-customer-', ${added} + n),
  ${AMOUNT},
  REPEAT('order detail ', 16),
  NOW()
FROM seq;
SQL
}

echo "Resetting orders and seeding $BASE_ROWS base orders ..."
ROWS="$BASE_ROWS" MYSQL_CONTAINER="$CONTAINER" DB_NAME="$DATABASE" \
  DB_USERNAME="$USER" DB_PASSWORD="$PASSWORD" \
  "$(dirname "$0")/seed.sh" >/dev/null

printf '%-12s | %s\n' "cheap_orders" "elapsed"
printf '%-12s-+-%s\n' "------------" "--------"
printf '%-12d | %ss\n' 0 "$(measure_search)"

added=0
while ((added < TOTAL)); do
  insert_batch
  added=$((added + BATCH_SIZE))
  printf '%-12d | %ss\n' "$added" "$(measure_search)"
done
