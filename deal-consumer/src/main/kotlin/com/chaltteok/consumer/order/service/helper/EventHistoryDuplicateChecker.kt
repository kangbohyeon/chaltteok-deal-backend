package com.chaltteok.consumer.order.service.helper

import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class EventHistoryDuplicateChecker(
    private val eventHistoryRepository: EventHistoryRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun isDuplicate(user: User, timeSaleStock: TimeSaleStock): Boolean {
        return try {
            eventHistoryRepository.saveAndFlush(EventHistory(user = user, timeSaleStock = timeSaleStock))
            false
        } catch (e: DataIntegrityViolationException) {
            log.warn { "중복 구매 시도 감지 — userId=${user.id}, timeSaleStockId=${timeSaleStock.id}" }
            true
        }
    }
}
