package com.chaltteok.user.dailystock.service

import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.user.dailystock.dto.OpenDailyStockResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DailyStockQueryServiceImpl(
    private val dailyStockRepository: DailyStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
) : DailyStockQueryService {

    @Transactional(readOnly = true)
    override fun getOpenDailyStocks(): List<OpenDailyStockResponse> {
        val now = LocalDateTime.now()
        val legacy = dailyStockRepository.findAllByStatusWithProduct(DailyStockStatus.OPEN)
            .filter { it.startAt == null }
        val timeSale = dailyStockRepository.findActiveTimeSaleStocks(now)
        return (legacy + timeSale).map { OpenDailyStockResponse.from(it) }
    }

    @Transactional(readOnly = true)
    override fun getParticipationCounts(userId: Long): Map<String, Int> =
        eventHistoryRepository.findAllWithStockByUserId(userId)
            .groupBy { it.dailyStock.stockUuid }
            .mapValues { it.value.size }
}
