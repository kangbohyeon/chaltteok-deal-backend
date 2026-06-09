package com.chaltteok.user.stats.service

import com.chaltteok.core.domain.OrderStats
import com.chaltteok.core.repository.orderstats.OrderStatsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class OrderStatsServiceImpl(
    private val orderStatsRepository: OrderStatsRepository,
) : OrderStatsService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun incrementOrderStats(date: LocalDate, revenue: Long) {
        val stats = orderStatsRepository.findByStatDateWithLock(date)
            ?: orderStatsRepository.save(OrderStats(statDate = date))
        stats.orderCount++
        stats.totalRevenue += revenue
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun incrementCancelStats(date: LocalDate) {
        val stats = orderStatsRepository.findByStatDateWithLock(date)
            ?: orderStatsRepository.save(OrderStats(statDate = date))
        stats.cancelledCount++
    }
}
