package com.chaltteok.consumer.order.service.helper

import com.chaltteok.consumer.notification.NotificationSaveHelper
import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

private val log = KotlinLogging.logger {}

@Component
class StockDecrementHelper(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val notificationSaveHelper: NotificationSaveHelper,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun tryDecrement(timeSaleStockId: Long, quantity: Int): Boolean {
        val stock = timeSaleStockRepository.findByIdWithProduct(timeSaleStockId)
            ?: throw OrderProcessingException("TimeSaleStock not found: $timeSaleStockId")
        if (stock.status != TimeSaleStockStatus.OPEN) {
            log.warn { "비정상 상태 재고 차감 시도 무시 — stockId=$timeSaleStockId, status=${stock.status}" }
            return false
        }
        if (stock.remainStock < quantity) {
            stock.markSoldOutIfDepleted()
            return false
        }
        stock.decrease(quantity)
        if (stock.status == TimeSaleStockStatus.SOLD_OUT) {
            // 재고 차감 커밋 후 별도 트랜잭션에서 알림 저장 — 알림 실패가 재고 차감을 롤백하지 않음
            val notification = Notification.forSoldOut(stock.product.name)
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    notificationSaveHelper.save(notification)
                }
            })
        }
        return true
    }
}
