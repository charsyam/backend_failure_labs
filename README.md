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

## 실습

각 lab1, lab2, lab3, lab4, lab5 안의 README.md를 참고해서 진행하세요.
