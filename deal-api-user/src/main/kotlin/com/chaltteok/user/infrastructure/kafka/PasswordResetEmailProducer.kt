package com.chaltteok.user.infrastructure.kafka

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.event.PasswordResetRequestedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class PasswordResetEmailProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun sendPasswordResetRequested(email: String, tempPassword: String) {
        val payload = objectMapper.writeValueAsString(PasswordResetRequestedEvent(email, tempPassword))
        kafkaTemplate.send(KafkaTopics.PASSWORD_RESET_EMAILS, email, payload)
            .whenComplete { result, ex ->
                if (ex != null) {
                    log.error(ex) { "비밀번호 재설정 이메일 발행 실패 — email=$email" }
                } else {
                    log.info { "비밀번호 재설정 이메일 발행 성공 — offset=${result.recordMetadata.offset()}" }
                }
            }
    }
}
