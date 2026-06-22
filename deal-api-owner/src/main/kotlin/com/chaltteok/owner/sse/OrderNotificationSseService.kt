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
        // 기존 emitter를 먼저 정상 종료 — 구버전 onCompletion이 신규 emitter를 삭제하는 race condition 방지
        emitters.put(userId, emitter)?.complete()
        // 조건부 삭제: map에 이 emitter가 있을 때만 제거 (신규 emitter 보호)
        emitter.onCompletion { emitters.remove(userId, emitter) }
        emitter.onTimeout { emitters.remove(userId, emitter) }
        emitter.onError { emitters.remove(userId, emitter) }
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

    // 프록시(nginx 60s, Next.js dev) timeout 방지 — 15초마다 ping 전송
    @Scheduled(fixedDelay = 15_000)
    fun heartbeat() {
        if (emitters.isEmpty()) return
        val deadKeys = mutableListOf<Long>()
        emitters.forEach { (userId, emitter) ->
            runCatching {
                emitter.send(SseEmitter.event().name("ping").data("ok"))
            }.onFailure {
                log.debug { "SSE heartbeat 실패 — userId=$userId (연결 종료)" }
                deadKeys += userId
            }
        }
        deadKeys.forEach { emitters.remove(it) }
    }
}
