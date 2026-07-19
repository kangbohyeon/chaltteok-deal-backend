dependencies {
    implementation(project(":deal-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("net.javacrumbs.shedlock:shedlock-spring:5.16.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:5.16.0")
}
