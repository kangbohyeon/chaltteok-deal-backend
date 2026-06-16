package com.chaltteok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DealConsumerApplication

fun main(args: Array<String>) {
    runApplication<DealConsumerApplication>(*args)
}
