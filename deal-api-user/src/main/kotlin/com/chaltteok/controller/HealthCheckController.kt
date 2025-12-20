package com.chaltteok.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/v1/orders"])
class HealthCheckController {

    @GetMapping(value = ["/healthcheck"])
    fun healthCheck(): String {
        return "OK"
    }
}