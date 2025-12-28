plugins {
    `java-library`
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("idea")
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

idea {
    module {
        val kaptMainWorkerDependencies = configurations.getByName("kapt")
        sourceDirs.add(file("build/generated/source/kapt/main"))
        generatedSourceDirs.add(file("build/generated/source/kapt/main"))
    }
}

tasks.bootJar {
    enabled = false // 실행 가능한 Jar(서버용) 생성을 끔
}

tasks.jar {
    enabled = true // 일반 Jar(라이브러리용) 생성을 켬
}