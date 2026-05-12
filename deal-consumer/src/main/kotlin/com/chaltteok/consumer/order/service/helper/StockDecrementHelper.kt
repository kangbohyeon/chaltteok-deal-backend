package com.chaltteok.consumer.order.service.helper

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
    fun tryDecrement(dailyStockId: Long): Boolean {
        val stock = dailyStockRepository.findById(dailyStockId)
            .orElseThrow { RuntimeException("DailyStock not found: $dailyStockId") }
        if (stock.remainStock <= 0) return false
        stock.remainStock -= 1
        if (stock.remainStock == 0) stock.status = DailyStockStatus.SOLD_OUT
        return true
    }
}
