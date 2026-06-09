package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCompletedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val log = KotlinLogging.logger {}

// confirmOrder() 트랜잭션 커밋 후 이메일 직접 발송 (Kafka 재발행 없음)
@Component
class OrderEmailEventListener(private val emailService: EmailService) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: OrderCompletedEvent) {
        try {
            emailService.sendOrderConfirmation(event)
            log.info { "주문 확인 이메일 발송 완료 — orderNumber=${event.orderNumber}" }
        } catch (ex: Exception) {
            log.error(ex) { "주문 확인 이메일 발송 실패 — orderNumber=${event.orderNumber}" }
        }
    }
}
