# LAB 5 - Wallet 잠금을 걸었는데 중복 결제가 발생한다

Wallet에 비관적 잠금을 걸고 결제 전 주문을 일괄 처리합니다. 두 요청을 동시에 보내면 Wallet 잠금으로 직렬화될 것처럼
보이지만, 일부 실행에서 같은 Order의 결제 history가 두 번 기록됩니다. 코드와 SQL 로그를 보고 원인을 찾아보세요.

## 실행

```bash
docker compose up -d mysql
cd lab5
../gradlew bootRun
```

다른 터미널에서 재현합니다.

```bash
bash lab5/scripts/reproduce.sh
```

## 테스트 데이터 초기화 API

새로운 테스트를 시작할 때 `setup` API를 호출합니다.

```bash
curl -X POST 'localhost:8080/lab5/setup'
```

이 API는 기존 `lab5_payment_histories`, `lab5_orders`, `lab5_wallets` 데이터를 모두 비운 뒤 다음 기본값으로
테스트 데이터를 다시 만듭니다.

```text
Wallet: 1개
Wallet 잔액: 100000
Order: 100개
Order별 금액: 100
Order 상태: BEFORE_PAYMENT
```

응답에 이후 결제 API에서 사용할 `walletId`가 포함됩니다.

```json
{
  "walletId": 1,
  "orderCount": 100,
  "orderAmount": 100,
  "walletBalance": 100000
}
```

필요하면 초기값을 변경할 수 있습니다.

```bash
curl -X POST \
  'localhost:8080/lab5/setup?orderCount=200&orderAmount=500&walletBalance=200000'
```

`setup`은 lab5 테스트 데이터를 삭제하고 다시 생성하므로 실행 중인 결제 요청이 없을 때 호출해야 합니다.

정상이라면 완료된 Order는 100건이고 history도 100건이어야 합니다. 문제가 재현되면 완료 Order는 100건인데
history는 200건이며 `duplicatedOrderHistories`에 같은 Order별로 `2`가 표시됩니다.

코드와 SQL 로그를 비교하여 두 요청이 같은 주문을 처리하게 되는 원인을 찾고, 중복 결제를 막을 방법을 제시하세요.
