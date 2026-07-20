package com.chaltteok.user.timesalestock.service

import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.user.timesalestock.dto.OpenTimeSaleStockResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TimeSaleStockQueryServiceImpl(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
) : TimeSaleStockQueryService {

    @Transactional(readOnly = true)
    override fun getVisibleTimeSaleStocks(): List<OpenTimeSaleStockResponse> {
        val now = LocalDateTime.now()
        val legacy = timeSaleStockRepository.findAllByStatusWithProduct(TimeSaleStockStatus.OPEN)
            .filter { it.startAt == null }
        val timeSale = timeSaleStockRepository.findVisibleTimeSaleStocks(now)
        return (legacy + timeSale).map { OpenTimeSaleStockResponse.from(it) }
    }

    @Transactional(readOnly = true)
    override fun getParticipationCounts(userId: Long): Map<String, Int> =
        eventHistoryRepository.findAllWithStockByUserId(userId)
            .groupBy { it.timeSaleStock.stockUuid }
            .mapValues { it.value.size }
}
