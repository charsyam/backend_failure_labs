#!/usr/bin/env bash
set -euo pipefail

# orders 테이블에 초기 주문 데이터를 넣는다.
# 전체 ROWS 건 중 약 1% 만 amount 가 0~499 이고, 나머지는 500 이상이다.

CONTAINER="${MYSQL_CONTAINER:-db-edu-mysql}"
DATABASE="${DB_NAME:-db_edu}"
USER="${DB_USERNAME:-db_edu}"
PASSWORD="${DB_PASSWORD:-db_edu}"
ROWS="${ROWS:-500000}"

echo "Seeding $ROWS orders ..."

docker exec -i "$CONTAINER" mysql -u"$USER" -p"$PASSWORD" "$DATABASE" 2>/dev/null <<SQL
SET cte_max_recursion_depth = ${ROWS};

TRUNCATE TABLE orders;

INSERT INTO orders (customer, amount, note, created_at)
WITH RECURSIVE seq (n) AS (
  SELECT 1
  UNION ALL
  SELECT n + 1 FROM seq WHERE n < ${ROWS}
)
SELECT
  CONCAT('customer-', n),
  CASE WHEN n % 100 = 0 THEN n % 500 ELSE 500 + (n % 999500) END,
  REPEAT('order detail ', 16),
  NOW()
FROM seq;

ANALYZE TABLE orders;

SELECT
  COUNT(*)                                        AS total_rows,
  SUM(amount < 500)                               AS matched_lt_500,
  ROUND(SUM(amount < 500) / COUNT(*) * 100, 2)    AS matched_pct
FROM orders;
SQL

echo "done."
