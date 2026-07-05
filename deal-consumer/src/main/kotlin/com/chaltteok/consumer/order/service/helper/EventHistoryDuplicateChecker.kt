package com.chaltteok.consumer.order.service.helper

import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class EventHistoryDuplicateChecker(
    private val eventHistoryRepository: EventHistoryRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    fun isExceedingPurchaseLimit(user: User, timeSaleStock: TimeSaleStock, quantity: Int): Boolean {
        val maxPurchaseCount = timeSaleStock.maxPurchaseCount ?: return false
        val userId = user.id ?: error("인증된 사용자의 ID가 null입니다")
        val participated = eventHistoryRepository.countByUser_IdAndTimeSaleStock_Id(userId, timeSaleStock.id)
        val exceeds = participated + quantity > maxPurchaseCount
        if (exceeds) {
            log.warn { "구매 한도 초과 감지 — userId=${user.id}, timeSaleStockId=${timeSaleStock.id}, participated=$participated, requested=$quantity, max=$maxPurchaseCount" }
        }
        return exceeds
    }
}
