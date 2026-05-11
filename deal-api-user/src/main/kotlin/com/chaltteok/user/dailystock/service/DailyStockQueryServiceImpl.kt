package com.chaltteok.user.dailystock.service

import com.chaltteok.core.domain.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.user.dailystock.dto.OpenDailyStockResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyStockQueryServiceImpl(
    private val dailyStockRepository: DailyStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
) : DailyStockQueryService {

    @Transactional(readOnly = true)
    override fun getOpenDailyStocks(): List<OpenDailyStockResponse> =
        dailyStockRepository.findAllByStatusWithProduct(DailyStockStatus.OPEN)
            .map { OpenDailyStockResponse.from(it) }

    @Transactional(readOnly = true)
    override fun getParticipatedStockIds(userId: Long): List<Long> =
        eventHistoryRepository.findAllByUser_Id(userId)
            .mapNotNull { it.dailyStock.id }
}
