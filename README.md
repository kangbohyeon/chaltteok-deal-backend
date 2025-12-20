
```
chaltteok-deal-backend (Root)
├── settings.gradle.kts
├── deal-core         [Library] 도메인, Entity, Repository, QueryDSL, Utils
├── deal-common-api   [Library] API 공통설정 (Response, Exception, Security Config)
├── deal-gateway      [App] (Spring Cloud Gateway, Netty) -> 8000 포트
├── deal-api-user     [App] 사용자용(Spring Boot REST API) -> 8080 포트
├── deal-api-owner    [App] 점주용(Spring Boot REST API) -> 8081 포트
├── deal-api-admin    [App] 관리자용(Spring Boot REST API) -> 8082 포트
└── deal-consumer     [App] Kafka 메세지 처리 워커 -> 8083 포트
```
