# LAB 6 - 데이터 분포 변화와 느려지는 조회

처음에는 `amount < 500`인 주문이 없지만, 서비스 운영 중 소액 주문이 계속 추가됩니다.
주문 검색 API는 오래된 순서로 20건만 반환합니다. 데이터가 쌓이는 동안 응답 시간과 실행계획이 어떻게 변하는지
관찰하고 원인을 찾아보세요.

## 실행

MySQL을 실행하고 초기 데이터를 준비합니다.

```bash
docker compose up -d mysql
bash lab6/scripts/seed.sh
```

애플리케이션을 실행합니다.

```bash
cd lab6
../gradlew bootRun
```

## 재현

별도 터미널에서 검색 응답 시간을 관찰합니다.

```bash
bash lab6/scripts/watch-search.sh
```

다른 터미널에서 소액 주문을 계속 추가합니다.

```bash
bash lab6/scripts/generate-load.sh
```

실행계획을 확인할 수 있습니다.

```bash
curl -X POST 'localhost:8080/orders/explain?amountLessThan=500'
curl -X POST 'localhost:8080/orders/explain-analyze?amountLessThan=500'
```

`watch-search.sh`는 `Ctrl+C`로 종료합니다. 환경에 따라 실행계획이 바뀌는 시점이 다르므로 기본 적재량으로
현상이 나타나지 않으면 `TOTAL`과 `RATE`를 높여 다시 실행할 수 있습니다.

```bash
TOTAL=10000 RATE=50 bash lab6/scripts/generate-load.sh
```
