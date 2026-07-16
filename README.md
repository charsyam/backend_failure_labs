# Spring Boot + JPA 장애 재현 실습

Spring Boot + Kotlin + JPA + MySQL 8 환경에서 운영 중 발생할 수 있는 DB/JPA 장애를 재현하는 실습 프로젝트입니다.

이 코드는 의도적으로 문제 상황만 포함합니다. Optimistic Lock, Pessimistic Lock, Unique Constraint, Batch Insert, Cursor Pagination, Covering Index 같은 해결책은 구현하지 않았습니다.

## 실행

```bash
docker compose up -d
./gradlew bootRun
```

기본 API 주소는 `http://localhost:8080` 입니다.

## LAB 1 Lost Update

서로 다른 API가 같은 `User` row를 동시에 읽고 각각 `likes`, `comments`를 1씩 증가시키면서, 나중에 커밋한 트랜잭션이 먼저 커밋한 변경을 덮어씁니다.

```bash
curl -X POST localhost:8080/labs/1-lost-update/reset

curl -X POST 'localhost:8080/labs/1-lost-update/users/1/unsafe-likes?delta=1' &
curl -X POST 'localhost:8080/labs/1-lost-update/users/1/unsafe-comments?delta=1' &
wait

sleep 1
curl localhost:8080/labs/1-lost-update/users/1
```

정상이라면 최종값은 `likes=1`, `comments=1`이어야 합니다. 최종값이 `likes=0, comments=1` 또는 `likes=1, comments=0`이면 한쪽 변경이 사라진 것입니다.

동시 호출 타이밍을 직접 맞추기 어렵다면 스크립트를 사용합니다.

```bash
chmod +x scripts/lab1-lost-update.sh
ROUNDS=100 ./scripts/lab1-lost-update.sh
```

## LAB 2 Batch Insert 문제

1000건을 `save()` 반복 호출로 저장합니다. 건수가 커질수록 영속성 컨텍스트와 insert round-trip 비용이 커집니다.

```bash
curl -X DELETE localhost:8080/labs/2-batch-insert/comments
curl -X POST 'localhost:8080/labs/2-batch-insert/save-loop?rows=1000'
```

## LAB 3 COUNT 성능 문제

`status` 인덱스 없이 조건 COUNT를 실행합니다. `EXPLAIN`에서 full scan 여부를 확인합니다.

```bash
curl -X POST 'localhost:8080/labs/3-count/seed?rows=100000&paidRatioPercent=80'
curl -X POST localhost:8080/labs/3-count/slow-count
```

## LAB 4 Optimizer 실행계획 문제

데이터 분포를 바꾸고 같은 조건 쿼리의 실행계획을 관찰합니다. 이 샘플은 인덱스를 제공하지 않으므로 기본적으로 나쁜 계획/스캔 비용을 확인하는 용도입니다.

```bash
curl -X POST 'localhost:8080/labs/4-optimizer/seed-selective?rows=100000'
curl -X POST 'localhost:8080/labs/4-optimizer/explain?amountLessThan=500'
curl -X POST 'localhost:8080/labs/4-optimizer/seed-low-selectivity?rows=100000'
curl -X POST 'localhost:8080/labs/4-optimizer/explain-analyze?amountLessThan=500'
```

## LAB 5 Transaction Visibility

트랜잭션 내부에서 주문을 생성한 뒤 커밋 전에 상대 서비스 큐로 주문 id 메시지를 보냅니다. 상대 서비스 consumer가 메시지를 받자마자 우리 주문 조회 API를 호출하면 아직 커밋 전이라 404를 받을 수 있습니다.

```bash
curl -X POST 'localhost:8080/labs/5-transaction-visibility/bad?userId=1&sleepBeforeCommitMs=1000'
curl 'localhost:8080/labs/5-transaction-visibility/partner-attempts'
```

## LAB 6 Check-Then-Insert

동일 사용자의 동일 정책 쿠폰 발급 요청이 동시에 들어오면 `exists` 확인 후 insert 사이에서 중복 발급이 발생합니다.

```bash
curl -X DELETE localhost:8080/labs/6-check-then-insert/coupons

curl -X POST 'localhost:8080/labs/6-check-then-insert/unsafe?userId=1&policyId=100&sleepMs=2000' &
curl -X POST 'localhost:8080/labs/6-check-then-insert/unsafe?userId=1&policyId=100&sleepMs=2000' &
wait
```

## LAB 7 Entity 조회 문제

필요한 컬럼만 조회하지 않고 `Order` Entity 전체를 조회합니다.

```bash
curl -X POST 'localhost:8080/labs/7-projection-covering-index/seed?rows=100000'
curl 'localhost:8080/labs/7-projection-covering-index/entity?status=PAID&size=100'
```

## LAB 8 Offset Pagination 문제

대량 데이터에서 깊은 page를 offset 방식으로 조회합니다.

```bash
curl -X POST 'localhost:8080/labs/8-pagination/seed?rows=200000&userId=1'
curl 'localhost:8080/labs/8-pagination/offset?userId=1&page=1000&size=20'
```
