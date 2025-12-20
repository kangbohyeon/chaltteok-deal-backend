package com.chaltteok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["com.chaltteok"]
)
class DealUserApplication

fun main(args: Array<String>) {
    runApplication<DealUserApplication>(*args)
}