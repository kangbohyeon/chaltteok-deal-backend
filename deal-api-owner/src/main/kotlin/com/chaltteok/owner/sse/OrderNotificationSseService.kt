package com.chaltteok.owner.sse

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

private val log = KotlinLogging.logger {}

@Service
class OrderNotificationSseService {

    private val emitters = ConcurrentHashMap<Long, SseEmitter>()

    fun subscribe(userId: Long): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        emitters[userId] = emitter
        emitter.onCompletion { emitters.remove(userId) }
        emitter.onTimeout { emitters.remove(userId) }
        emitter.onError { emitters.remove(userId) }
        runCatching {
            emitter.send(SseEmitter.event().name("connected").data("ok"))
        }
        log.info { "SSE 구독 등록 — userId=$userId, 연결 수=${emitters.size}" }
        return emitter
    }

    fun broadcast(eventName: String, data: Any) {
        if (emitters.isEmpty()) {
            log.warn { "SSE 브로드캐스트 — 구독자 없음 (eventName=$eventName)" }
            return
        }
        val deadKeys = mutableListOf<Long>()
        emitters.forEach { (userId, emitter) ->
            runCatching {
                emitter.send(SseEmitter.event().name(eventName).data(data))
                log.info { "SSE 이벤트 전송 완료 — userId=$userId, eventName=$eventName" }
            }.onFailure {
                log.warn { "SSE 전송 실패 — userId=$userId" }
                deadKeys += userId
            }
        }
        deadKeys.forEach { emitters.remove(it) }
    }

    // nginx proxy_read_timeout(기본 60s) 방지 — 30초마다 SSE 주석 전송
    @Scheduled(fixedDelay = 30_000)
    fun heartbeat() {
        if (emitters.isEmpty()) return
        val deadKeys = mutableListOf<Long>()
        emitters.forEach { (userId, emitter) ->
            runCatching {
                emitter.send(SseEmitter.event().comment("ping"))
            }.onFailure {
                log.debug { "SSE heartbeat 실패 — userId=$userId (연결 종료)" }
                deadKeys += userId
            }
        }
        deadKeys.forEach { emitters.remove(it) }
    }
}
