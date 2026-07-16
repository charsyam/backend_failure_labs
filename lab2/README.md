# Mini SNS - Batch Insert 실습

게시글 목록을 하나의 HTTP 요청으로 받아 JPA `save()` 반복으로 저장하는 LAB 2의 시작 프로젝트입니다.

LAB 1과 같은 MySQL의 `db_edu` 데이터베이스 및 `posts` 테이블을 사용합니다. 기존 schema와 데이터는 삭제하지 않습니다.

## 실행

먼저 저장소 루트에서 MySQL을 실행합니다.

```bash
docker compose up -d mysql
```

lab2는 자체 Gradle 프로젝트이므로 `lab2` 디렉토리에서 루트의 Gradle wrapper로 실행합니다.

```bash
cd lab2
../gradlew bootRun
```

## 게시글 목록 추가

저장할 건수는 `count` 파라미터로 전달하고, 실제 데이터는 서버가 생성합니다. 요청 페이로드 생성·전송 비용이 측정에 섞이지 않도록 하기 위함입니다.

```bash
curl -X POST 'localhost:8080/posts/bulk?count=2'
```

응답에는 요청 건수, 저장 건수, 애플리케이션에서 측정한 저장 시간이 포함됩니다.

```json
{
  "requestedCount": 2,
  "savedCount": 2,
  "elapsedMillis": 37,
  "method": "save() loop"
}
```

## 1,000건 저장

```bash
bash lab2/scripts/create-1000-posts.sh
```

건수는 환경 변수로 변경할 수 있습니다.

```bash
COUNT=5000 bash lab2/scripts/create-1000-posts.sh
```

10만 건 비교도 같은 스크립트로 실행할 수 있습니다.

```bash
COUNT=100000 bash lab2/scripts/create-1000-posts.sh
```

## 목록 확인

```bash
curl 'localhost:8080/posts?limit=20'
```

현재 구현은 루프 안에서 `save()`를 1,000번 호출하고 `Post`가 `IDENTITY` 식별자 전략을 사용합니다. 실행 로그에서 INSERT SQL이 건수만큼 각각 실행되는 것을 확인할 수 있습니다. `saveAll()`, Hibernate Batch, JdbcTemplate Batch 개선은 아직 적용하지 않은 기준 상태입니다.
