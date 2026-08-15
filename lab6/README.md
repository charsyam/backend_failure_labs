# LAB 6 - 데이터 분포 변화와 느려지는 조회

처음에는 `amount < 500`인 주문이 없지만, 서비스 운영 중 소액 주문이 계속 추가됩니다.
주문 검색 API는 오래된 순서로 20건만 반환합니다. 데이터가 쌓이는 동안 응답 시간이 어떻게 변하는지 관찰하고
원인을 찾아보세요.

## 실행

MySQL을 실행합니다.

```bash
docker compose up -d mysql
```

애플리케이션을 실행합니다.

```bash
cd lab6
../gradlew bootRun
```

## 재현

별도 터미널에서 재현 스크립트를 실행합니다.

```bash
bash lab6/scripts/generate-load.sh
```

스크립트는 기존 주문을 고액 주문 100만 건으로 초기화합니다. 이후 소액 주문을 500건씩 추가하면서 매 단계의
검색 응답 시간을 출력합니다.

```bash
cheap_orders | elapsed
-------------+---------
0            | 0.004s
500          | 0.005s
1000         | 0.006s
```

환경에 따라 응답 시간이 바뀌는 시점이 다릅니다. 초기 주문 수, 한 번에 추가할 주문 수, 총 추가 건수를 변경할
수 있습니다.

```bash
BASE_ROWS=2000000 BATCH_SIZE=500 TOTAL=30000 bash lab6/scripts/generate-load.sh
```

재현 스크립트는 실행할 때마다 `orders` 데이터를 모두 삭제하고 다시 생성합니다.
