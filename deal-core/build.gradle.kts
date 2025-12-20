plugins {
    `java-library`
    kotlin("plugin.jpa")
    kotlin("kapt")
}


dependencies {
    // DB & JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    // QueryDSL
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")

    // Redis & Kafka
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.kafka:spring-kafka")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}