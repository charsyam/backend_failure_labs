#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

setup_response=$(curl -fsS -X POST "$BASE_URL/lab5/setup?orderCount=100&orderAmount=100&walletBalance=100000")
wallet_id=$(printf '%s' "$setup_response" | jq -r '.walletId')

curl -fsS -X POST "$BASE_URL/lab5/wallets/$wallet_id/pay-all" > /tmp/lab5-payment-1.json &
first_pid=$!
curl -fsS -X POST "$BASE_URL/lab5/wallets/$wallet_id/pay-all" > /tmp/lab5-payment-2.json &
second_pid=$!

wait "$first_pid"
wait "$second_pid"

curl -fsS "$BASE_URL/lab5/wallets/$wallet_id/result" | jq .
