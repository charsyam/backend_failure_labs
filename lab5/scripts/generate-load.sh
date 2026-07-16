#!/usr/bin/env bash
set -euo pipefail

# 소액 주문(amount=0)을 천천히 계속 추가한다.
# 학생은 다른 터미널에서 watch-search.sh 로 조회 시간을 지켜보며,
# 소액 주문이 쌓이다가 어느 순간 조회가 느려지는 지점을 관찰한다.

BASE_URL="${BASE_URL:-http://localhost:8080}"
AMOUNT="${AMOUNT:-0}"       # 추가할 소액 주문의 amount
RATE="${RATE:-10}"          # 초당 추가 건수
TOTAL="${TOTAL:-4000}"      # 총 추가 건수

interval="$(python3 -c "print(1.0 / $RATE)")"

echo "Adding $TOTAL orders (amount=$AMOUNT) at ~$RATE/s ..."

added=0
while [ "$added" -lt "$TOTAL" ]; do
  curl -fsS -X POST "$BASE_URL/orders?amount=$AMOUNT" >/dev/null
  added=$((added + 1))
  if [ $((added % 100)) -eq 0 ]; then
    echo "  added=$added"
  fi
  sleep "$interval"
done

echo "done. added=$added"
