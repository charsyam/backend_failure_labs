# Mini SNS - Lost Update 실습

Spring Boot, Kotlin, JPA에서 서로 다른 트랜잭션이 같은 `posts` 행을 수정할 때 발생하는 Lost Update 예제입니다.

## 실행

저장소 루트에서 공용 MySQL을 먼저 실행합니다.

```bash
docker compose up -d mysql
```

lab1은 자체 Gradle 프로젝트이므로 `lab1` 디렉토리에서 루트의 Gradle wrapper로 실행합니다.

```bash
cd lab1
../gradlew bootRun
```

MySQL은 루트 Compose의 `db_edu` 데이터베이스와 named volume을 사용합니다. 이후 lab도 같은 Compose와 DB를 사용하며, schema를 보존하려면 `docker compose down -v`를 실행하지 마세요.

## API

```bash
# 실습용 게시글 초기화
curl -X POST localhost:8080/posts/reset

# 게시글 조회
curl localhost:8080/posts/1

# likes 1 증가
curl -X POST localhost:8080/posts/1/likes

# comments 1 증가
curl -X POST localhost:8080/posts/1/comments
```

## 문제 재현

```bash
bash lab1/scripts/reproduce-lost-update.sh
```

스크립트는 기본적으로 좋아요 증가 API와 댓글 증가 API를 각각 1,000회씩, 동시성 50으로 호출합니다. 정상 결과는 `likes=1000`, `comments=1000`이어야 합니다. 하지만 현재 Entity에는 낙관적 락, 비관적 락, 원자적 update query가 없으며 Hibernate가 행의 다른 값까지 함께 update합니다. 따라서 실제 결과는 기대값보다 작아질 수 있습니다.

호출 횟수와 동시성은 환경 변수로 변경할 수 있습니다.

```bash
ROUNDS=2000 CONCURRENCY=100 bash lab1/scripts/reproduce-lost-update.sh
```

이 프로젝트는 장애 원인을 실습하기 위한 시작 상태이므로 해결 코드는 의도적으로 포함하지 않습니다.
