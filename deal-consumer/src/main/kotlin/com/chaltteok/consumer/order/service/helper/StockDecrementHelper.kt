package com.chaltteok.consumer.order.service.helper

import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.event.StockSoldOutEvent
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class StockDecrementHelper(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val outboxEventWriter: OutboxEventWriter,
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
            outboxEventWriter.write(
                source = OutboxEvent.SOURCE_CONSUMER_NOTIFICATION,
                aggregateId = stock.stockUuid,
                eventType = OutboxEvent.TYPE_STOCK_SOLD_OUT,
                event = StockSoldOutEvent(productName = stock.product.name),
            )
        }
        return true
    }
}
