# Mini SNS - COUNT 최적화 실습

전체 페이지 내비게이션을 제공하기 위해 게시글 목록 요청마다 `COUNT(*)`를 실행하는 예제입니다. LAB 2에서 저장한 `posts` schema와 데이터를 그대로 사용합니다.

## 실행

저장소 루트에서 MySQL을 먼저 실행합니다.

```bash
docker compose up -d mysql
```

lab3은 자체 Gradle 프로젝트이므로 `lab3` 디렉토리에서 루트의 Gradle wrapper로 실행합니다.

```bash
cd lab3
../gradlew bootRun
```

## 페이지 조회

```bash
curl 'localhost:8080/posts/page?page=0&size=20'
```

응답은 현재 페이지의 게시글과 전체 데이터 수, 전체 페이지 수를 포함합니다.

```json
{
  "posts": [],
  "page": 0,
  "size": 20,
  "totalElements": 100000,
  "totalPages": 5000,
  "hasPrevious": false,
  "hasNext": true
}
```

이 API는 페이지를 요청할 때마다 다음 두 종류의 SQL을 실행합니다.

```sql
SELECT ... FROM posts ORDER BY id DESC LIMIT 20 OFFSET 0;
SELECT COUNT(*) FROM posts;
```

반복 호출은 다음 스크립트로 확인합니다.

```bash
REQUESTS=10 bash lab3/scripts/request-pages.sh
```

## 데이터 준비

`posts` 데이터는 LAB 2에서 적재합니다. LAB 2 애플리케이션을 실행한 뒤 아래처럼 게시글을 채우고 LAB 3로 넘어옵니다.

```bash
COUNT=100000 bash lab2/scripts/create-1000-posts.sh
```

## 실행계획 관찰

현재 `posts`의 인덱스 상태와 `COUNT(*)`의 실행계획을 직접 확인합니다.

```bash
docker exec -it db-edu-mysql mysql -udb_edu -pdb_edu db_edu \
  -e "SHOW INDEX FROM posts; EXPLAIN ANALYZE SELECT COUNT(*) FROM posts;"
```

`request-pages.sh`로 페이지 반복 호출 시간도 함께 측정해 둡니다.

이제 `COUNT(*)`가 왜 이렇게 느린지, 어떻게 하면 빠르게 만들 수 있을지 스스로 가설을 세우고 검증해 보세요. 실행계획이 무엇을 스캔하고 있는지, `posts`의 어떤 컬럼 구성이 그 스캔을 무겁게 만드는지 살펴보는 것이 출발점입니다.
