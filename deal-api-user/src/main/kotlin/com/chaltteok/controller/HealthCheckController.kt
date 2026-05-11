package com.chaltteok.controller

import com.chaltteok.common.dto.ResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/health")
class HealthCheckController {
    @GetMapping
    fun health(): ResponseDTO<String> = ResponseDTO.success("OK")
}
