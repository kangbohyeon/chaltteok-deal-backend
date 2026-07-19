package com.chaltteok.consumer.order.service.helper

import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class StockDecrementHelper(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val notificationRepository: NotificationRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun tryDecrement(timeSaleStockId: Long, quantity: Int): Boolean {
        val stock = timeSaleStockRepository.findByIdWithProduct(timeSaleStockId)
            ?: throw OrderProcessingException("TimeSaleStock not found: $timeSaleStockId")
        if (stock.remainStock < quantity) {
            if (stock.remainStock == 0) stock.status = TimeSaleStockStatus.SOLD_OUT
            return false
        }
        stock.decrease(quantity)
        if (stock.remainStock == 0) {
            notificationRepository.save(Notification.forSoldOut(stock.product.name))
        }
        return true
    }
}
