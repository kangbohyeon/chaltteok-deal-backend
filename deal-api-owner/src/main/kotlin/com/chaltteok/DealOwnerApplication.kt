package com.chaltteok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication(
    scanBasePackages = ["com.chaltteok.owner","com.chaltteok.core","com.chaltteok.common"]
)
@EntityScan(
    basePackages = ["com.chaltteok.core.domain"]
)
@EnableJpaRepositories(
    basePackages = ["com.chaltteok.core.repository"]
)
class DealOwnerApplication

fun main(args: Array<String>) {
    runApplication<DealOwnerApplication>(*args)
}