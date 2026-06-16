package com.chaltteok.consumer.email

import com.chaltteok.core.event.PasswordResetRequestedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class PasswordResetEmailConsumer(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = ["password-reset-emails"],
        groupId = "deal-password-reset-email-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, PasswordResetRequestedEvent::class.java)
        val maskedEmail = event.email.replace(Regex("(?<=.{2}).(?=.*@)"), "*")
        log.info { "비밀번호 재설정 이메일 발송 — to=$maskedEmail" }
        emailService.sendPasswordReset(event.email, event.tempPassword)
    }
}
