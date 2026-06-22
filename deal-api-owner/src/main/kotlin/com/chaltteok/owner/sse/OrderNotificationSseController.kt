package com.chaltteok.owner.sse

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/owner/notifications")
class OrderNotificationSseController(
    private val sseService: OrderNotificationSseService,
) {
    @GetMapping("/sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(authentication: Authentication, response: HttpServletResponse): SseEmitter {
        // 프록시(nginx) 버퍼링 방지 — 버퍼링 시 SSE 이벤트가 전달되지 않음
        response.setHeader("X-Accel-Buffering", "no")
        response.setHeader("Cache-Control", "no-cache")
        val userId = authentication.principal as Long
        return sseService.subscribe(userId)
    }
}
