package com.chaltteok.consumer.order.service.helper

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.EventHistory
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
    fun isDuplicate(user: User, dailyStock: DailyStock): Boolean {
        return try {
            eventHistoryRepository.saveAndFlush(EventHistory(user = user, dailyStock = dailyStock))
            false
        } catch (e: DataIntegrityViolationException) {
            log.warn { "중복 구매 시도 감지 — userId=${user.id}, dailyStockId=${dailyStock.id}" }
            true
        }
    }
}
