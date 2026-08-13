# LAB 4 - 간헐적인 검색 색인 누락

Item 생성과 검색 색인 요청은 정상적으로 처리되지만, 일부 Item이 검색 결과에서 간헐적으로 누락되는 예제입니다.
두 서비스의 코드와 로그를 확인해 누락 원인을 찾아보세요.

## 구성

- Item Service: `localhost:8080`, MySQL에 Item 저장 및 조회
- Search Service: `localhost:8081`, 색인 요청 처리 및 결과 기록

## 실행

터미널을 세 개 사용합니다.

```bash
docker compose up -d mysql
```

```bash
cd lab4
../gradlew :item-service:bootRun
```

```bash
cd lab4
../gradlew :search-service:bootRun
```

## 재현

재현 스크립트를 실행합니다.

```bash
bash lab4/scripts/reproduce.sh
```

생성 건수와 최대 대기 시간을 변경할 수 있습니다.

```bash
COUNT=1000 TIMEOUT_SECONDS=60 bash lab4/scripts/reproduce.sh
```

API 요청 자체는 모두 성공하지만 검색 결과에는 드물게 실패가 포함될 수 있습니다. 한 번에 재현되지 않으면
스크립트를 다시 실행합니다. 실습 API에는 타이밍이나 실패율을 조작하는 파라미터가 없습니다.

해답은 `lab4-solv`에 있습니다.
