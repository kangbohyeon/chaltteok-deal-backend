package com.chaltteok.user.infrastructure.kafka

import com.chaltteok.core.event.PasswordResetRequestedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val log = KotlinLogging.logger {}

@Component
class PasswordResetEventHandler(
    private val passwordResetEmailProducer: PasswordResetEmailProducer,
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onPasswordResetRequested(event: PasswordResetRequestedEvent) {
        try {
            passwordResetEmailProducer.sendPasswordResetRequested(event.email, event.tempPassword)
        } catch (ex: Exception) {
            log.error(ex) { "비밀번호 재설정 이메일 Kafka 발행 실패 — email=${event.email}" }
            throw ex
        }
    }
}
