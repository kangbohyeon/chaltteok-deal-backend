package com.chaltteok.core.service.orderstats

import com.chaltteok.core.repository.orderstats.OrderStatsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class OrderStatsServiceImpl(
    private val orderStatsRepository: OrderStatsRepository,
) : OrderStatsService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun incrementOrderStats(date: LocalDate, revenue: Long) {
        orderStatsRepository.upsertOrderStats(
            statUuid = UUID.randomUUID().toString(),
            statDate = date,
            revenue = revenue,
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun incrementCancelStats(date: LocalDate) {
        orderStatsRepository.upsertCancelStats(
            statUuid = UUID.randomUUID().toString(),
            statDate = date,
        )
    }
}
