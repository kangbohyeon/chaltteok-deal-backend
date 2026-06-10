package com.chaltteok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DealUserApplication

fun main(args: Array<String>) {
    runApplication<DealUserApplication>(*args)
}
