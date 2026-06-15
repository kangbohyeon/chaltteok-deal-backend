package com.chaltteok.consumer.order.service.helper

import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class StockDecrementHelper(
    private val dailyStockRepository: DailyStockRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun tryDecrement(dailyStockId: Long, quantity: Int): Boolean {
        val stock = dailyStockRepository.findById(dailyStockId)
            .orElseThrow { OrderProcessingException("DailyStock not found: $dailyStockId") }
        if (stock.remainStock < quantity) {
            if (stock.remainStock == 0) stock.status = DailyStockStatus.SOLD_OUT
            return false
        }
        stock.remainStock -= quantity
        if (stock.remainStock == 0) stock.status = DailyStockStatus.SOLD_OUT
        return true
    }
}
