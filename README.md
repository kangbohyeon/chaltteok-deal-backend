# chaltteok-deal-backend

찰떡(Chaltteok) 타임딜 플랫폼의 백엔드 모놀리식 모듈형 프로젝트입니다. Kotlin + Spring Boot 기반 멀티 모듈 구조로, 유저/점주/관리자 API를 별도 애플리케이션으로 분리하고 공통 도메인 로직은 라이브러리 모듈로 공유합니다.

## 기술 스택

- **언어/프레임워크:** Kotlin 1.9.25, Spring Boot 3.5.9, Java 21
- **데이터:** MySQL 8.0 (JPA + QueryDSL), Redis (캐싱 / Redisson 분산 락)
- **메시징:** Apache Kafka (Spring Kafka) — 주문/취소 후처리, Outbox + DLQ 패턴
- **게이트웨이:** Spring Cloud Gateway (Netty)
- **로깅:** Log4j2 + Disruptor
- **빌드:** Gradle (Kotlin DSL), 멀티 모듈

## 모듈 구조

```
chaltteok-deal-backend (Root)
├── settings.gradle.kts
├── deal-core         [Library] 도메인, Entity, Repository, QueryDSL, Redis/Kafka 공통 설정
├── deal-common-api   [Library] API 공통설정 (Response, Exception, Security Config)
├── deal-gateway      [App] Spring Cloud Gateway (Netty) → 8000 포트
├── deal-api-user     [App] 유저용 REST API → 8080 포트
├── deal-api-owner    [App] 점주용 REST API → 8081 포트
├── deal-api-admin    [App] 관리자용 REST API → 8082 포트
└── deal-consumer     [App] Kafka 메시지 처리 워커 → 8083 포트
```

| 모듈 | 역할 |
|------|------|
| `deal-core` | JPA 엔티티(불변 `class` + `val`), Repository, QueryDSL, Redis/Redisson, Kafka 공통 설정 |
| `deal-common-api` | 공통 응답 포맷, 예외 처리, Security Config 등 API 모듈 공통 베이스 |
| `deal-gateway` | 클라이언트 진입점, 라우팅 |
| `deal-api-user` | 회원가입/로그인/계정 잠금/비밀번호 재설정, 상품 조회/주문/결제, 댓글/문의 등 유저 도메인 API |
| `deal-api-owner` | 상품/재고/타임세일/배너 관리, 주문 관리, 대시보드 통계 등 점주 도메인 API |
| `deal-api-admin` | 운영자용 관리 API |
| `deal-consumer` | 주문/결제/알림 등 Kafka 이벤트 비동기 처리 워커 (Outbox + DLQ) |

## 핵심 아키텍처 규칙

- JPA 엔티티는 `data class` 금지 → `class` + `val` 불변 설계
- 모든 신규 테이블은 내부 Auto-Increment PK + 외부 노출용 UUID 분리
- 주문/재고 처리는 Redis 분산 락 + 낙관적 락 조합으로 동시성 제어
- 주문/취소 후처리는 Outbox 패턴 기반 Kafka 이벤트로 비동기 처리, 실패 시 DLQ 적재

## 로컬 실행

### 사전 요구사항

- JDK 21
- MySQL 8.0, Redis, Kafka (로컬 또는 Docker)

### 빌드

```bash
./gradlew build
```

### 모듈별 실행

```bash
./gradlew :deal-gateway:bootRun     # 8000
./gradlew :deal-api-user:bootRun    # 8080
./gradlew :deal-api-owner:bootRun   # 8081
./gradlew :deal-api-admin:bootRun   # 8082
./gradlew :deal-consumer:bootRun    # 8083
```

각 모듈의 `src/main/resources/application.yml`에서 DB/Redis/Kafka 연결 정보를 설정합니다.

### DB 마이그레이션

`db/migration` 디렉토리의 DDL 스크립트를 배포 환경에 순서대로 적용합니다.

## 브랜치 전략

- `main`: 운영 배포 브랜치
- `develop`: 개발 통합 브랜치
- `feature/*`: 기능 개발 브랜치 → `develop`로 Squash Merge
- `develop` → `main` 배포는 Release PR을 통해 Merge Commit으로 병합
