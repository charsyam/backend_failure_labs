# DB 실습 프로젝트

Spring Boot, Kotlin, JPA와 MySQL을 사용하는 데이터베이스 실습 프로젝트입니다. `lab1`부터 `lab5`까지 각각 독립된 Gradle 프로젝트이며, 모든 lab은 같은 MySQL 데이터베이스를 이어서 사용합니다.

## 준비

- Java 17
- Docker 및 Docker Compose
- `curl`
- Bash
- LAB 5 부하 생성 시 Python 3

저장소 루트에서 MySQL을 실행합니다.

```bash
docker compose up -d mysql
docker compose ps
```

MySQL 접속 정보의 기본값은 다음과 같습니다.

```text
host: localhost
port: 3307
database: db_edu
username: db_edu
password: db_edu
```

각 lab은 기본적으로 `http://localhost:8080`에서 실행됩니다. 한 번에 하나의 lab만 실행하고, 다음 lab을 시작하기 전에 실행 중인 애플리케이션을 `Ctrl+C`로 종료합니다.

데이터와 schema는 lab 사이에서 공유됩니다. 데이터를 유지하려면 `docker compose down -v`를 실행하지 않습니다.

## LAB 1

### 실행

저장소 루트에서 다음 명령을 실행합니다.

```bash
cd lab1
../gradlew bootRun
```

### 재현 및 확인

애플리케이션을 실행한 상태에서 새 터미널을 열고 저장소 루트에서 실행합니다.

```bash
bash lab1/scripts/reproduce-lost-update.sh
```

호출 횟수와 동시성을 변경할 수 있습니다.

```bash
ROUNDS=2000 CONCURRENCY=100 bash lab1/scripts/reproduce-lost-update.sh
```

API를 직접 호출하려면 다음 명령을 사용합니다.

```bash
curl -X POST http://localhost:8080/posts/reset
curl http://localhost:8080/posts/1
curl -X POST http://localhost:8080/posts/1/likes
curl -X POST http://localhost:8080/posts/1/comments
```

### 테스트

```bash
cd lab1
../gradlew test
```

## LAB 2

### 실행

```bash
cd lab2
../gradlew bootRun
```

### 재현 및 확인

애플리케이션을 실행한 상태에서 새 터미널을 열고 저장소 루트에서 실행합니다.

```bash
bash lab2/scripts/create-1000-posts.sh
curl 'http://localhost:8080/posts?limit=20'
```

저장 건수를 변경할 수 있습니다.

```bash
COUNT=5000 bash lab2/scripts/create-1000-posts.sh
COUNT=100000 bash lab2/scripts/create-1000-posts.sh
```

API를 직접 호출하려면 다음 명령을 사용합니다.

```bash
curl -X POST 'http://localhost:8080/posts/bulk?count=1000'
curl 'http://localhost:8080/posts?limit=20'
```

### 테스트

```bash
cd lab2
../gradlew test
```

## LAB 3

LAB 3는 `posts` 데이터를 사용합니다. 데이터가 없다면 LAB 2를 실행한 상태에서 먼저 데이터를 적재합니다.

```bash
COUNT=100000 bash lab2/scripts/create-1000-posts.sh
```

LAB 2를 종료한 뒤 LAB 3를 실행합니다.

### 실행

```bash
cd lab3
../gradlew bootRun
```

### 재현 및 확인

애플리케이션을 실행한 상태에서 새 터미널을 열고 저장소 루트에서 실행합니다.

```bash
curl 'http://localhost:8080/posts/page?page=0&size=20'
REQUESTS=10 bash lab3/scripts/request-pages.sh
```

페이지와 반복 횟수를 변경할 수 있습니다.

```bash
REQUESTS=20 PAGE=10 SIZE=50 bash lab3/scripts/request-pages.sh
```

MySQL에서 인덱스와 실행계획을 확인합니다.

```bash
docker exec -it db-edu-mysql mysql -udb_edu -pdb_edu db_edu \
  -e "SHOW INDEX FROM posts; EXPLAIN ANALYZE SELECT COUNT(*) FROM posts;"
```

### 테스트

```bash
cd lab3
../gradlew test
```

## LAB 4

### 실행

처음 실행해 `orders` 테이블을 생성합니다.

```bash
cd lab4
../gradlew bootRun
```

### 데이터 준비

애플리케이션을 실행한 상태에서 새 터미널을 열고 저장소 루트에서 실행합니다.

```bash
bash lab4/scripts/seed.sh
```

데이터 건수를 변경할 수 있습니다.

```bash
ROWS=100000 bash lab4/scripts/seed.sh
```

### 재현 및 확인

```bash
curl 'http://localhost:8080/orders/search?amountLessThan=500'
curl -X POST 'http://localhost:8080/orders/explain?amountLessThan=500'
curl -X POST 'http://localhost:8080/orders/explain-analyze?amountLessThan=500'
```

MySQL에서 직접 실행계획을 확인할 수도 있습니다.

```bash
docker exec -it db-edu-mysql mysql -udb_edu -pdb_edu db_edu \
  -e "SHOW INDEX FROM orders; EXPLAIN ANALYZE SELECT * FROM orders WHERE amount < 500;"
```

### 테스트

```bash
cd lab4
../gradlew test
```

## LAB 5

### 실행

처음 실행해 `orders` 테이블을 생성하거나 기존 schema를 확인합니다.

```bash
cd lab5
../gradlew bootRun
```

### 데이터 준비

애플리케이션을 실행한 상태에서 새 터미널을 열고 저장소 루트에서 실행합니다.

```bash
bash lab5/scripts/seed.sh
```

데이터 건수를 변경할 수 있습니다.

```bash
ROWS=200000 bash lab5/scripts/seed.sh
```

### 재현 및 확인

터미널 하나에서 조회 시간을 반복 확인합니다.

```bash
bash lab5/scripts/watch-search.sh
```

다른 터미널에서 부하를 생성합니다.

```bash
bash lab5/scripts/generate-load.sh
```

부하 크기와 속도를 변경할 수 있습니다.

```bash
TOTAL=1000 RATE=20 AMOUNT=0 bash lab5/scripts/generate-load.sh
```

조회 조건과 간격을 변경할 수 있습니다.

```bash
AMOUNT_LESS_THAN=500 INTERVAL=1 bash lab5/scripts/watch-search.sh
```

API를 직접 호출하려면 다음 명령을 사용합니다.

```bash
curl -X POST 'http://localhost:8080/orders?customer=buyer&amount=0'
curl 'http://localhost:8080/orders/search?amountLessThan=500'
curl -X POST 'http://localhost:8080/orders/explain?amountLessThan=500'
curl -X POST 'http://localhost:8080/orders/explain-analyze?amountLessThan=500'
```

### 테스트

```bash
cd lab5
../gradlew test
```

## 종료

애플리케이션은 실행 중인 터미널에서 `Ctrl+C`로 종료합니다. MySQL 컨테이너만 종료하려면 저장소 루트에서 실행합니다.

```bash
docker compose stop mysql
```

다시 시작할 때는 다음 명령을 사용합니다.

```bash
docker compose start mysql
```
