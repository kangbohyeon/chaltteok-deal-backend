package com.chaltteok.owner.product.scheduler

import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.product.ProductRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class ProductStockScheduler(
    private val productRepository: ProductRepository,
    private val dailyStockRepository: DailyStockRepository,
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    fun resetDailyStock() {
        val reset = productRepository.resetDailyStockForActiveProducts()
        productRepository.markZeroStockAsSoldOut()
        logger.info { "Daily stock reset completed: $reset products restored" }
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun openScheduledTimeSales() {
        val opened = dailyStockRepository.openScheduledStocks(LocalDateTime.now())
        if (opened > 0) {
            logger.info { "Opened $opened scheduled time sale stocks" }
        }
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun closeExpiredTimeSales() {
        val closed = dailyStockRepository.closeExpiredStocks(LocalDateTime.now())
        if (closed > 0) {
            logger.info { "Closed $closed expired time sale stocks" }
        }
    }
}
