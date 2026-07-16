#!/usr/bin/env bash
set -euo pipefail

# search 조회를 일정 간격으로 반복 호출하며 소요 시간을 출력한다.
# generate-load.sh 가 소액 주문을 쌓는 동안 이 값을 지켜보면,
# 어느 순간 조회 시간이 급격히 늘어나는 것을 관찰할 수 있다.

BASE_URL="${BASE_URL:-http://localhost:8080}"
AMOUNT_LESS_THAN="${AMOUNT_LESS_THAN:-500}"
INTERVAL="${INTERVAL:-2}"   # 조회 간격(초)

echo "Polling GET /orders/search?amountLessThan=$AMOUNT_LESS_THAN every ${INTERVAL}s ..."

while true; do
  curl -fsS "$BASE_URL/orders/search?amountLessThan=$AMOUNT_LESS_THAN" \
    | sed -e 's/[{}"]//g'
  echo
  sleep "$INTERVAL"
done
