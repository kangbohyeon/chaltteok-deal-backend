plugins {
    kotlin("jvm")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
dependencies {
    api(project(":deal-core"))
    api("org.springframework.boot:spring-boot-starter-web")
//    api("org.springframework.boot:spring-boot-starter-validation")
//    api("org.springframework.boot:spring-boot-starter-security") // 인증/인가 공통
}