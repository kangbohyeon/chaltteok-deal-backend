package com.chaltteok.owner.product.scheduler

import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class ProductStockScheduler(
    private val productRepository: ProductRepository,
    private val timeSaleStockRepository: TimeSaleStockRepository,
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    fun resetDailyStock() {
        val reset = productRepository.resetDailyStockForActiveProducts()
        productRepository.markZeroStockAsSoldOut()
        logger.info { "Daily stock reset completed: $reset products restored" }
    }

    // close → open 순서 직렬 실행: SCHEDULED 재고가 순간 OPEN으로 노출되는 경합을 방지 (이슈 #237)
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    fun syncTimeSaleStockStatuses() {
        val now = LocalDateTime.now()
        val closed = timeSaleStockRepository.closeExpiredStocks(now)
        val opened = timeSaleStockRepository.openScheduledStocks(now)
        if (closed > 0) {
            logger.warn { "만료 재고 CLOSED 전환: ${closed}건 (SCHEDULED 포함 가능 — 비정상 데이터 점검 권고)" }
        }
        if (opened > 0) {
            logger.info { "Opened $opened scheduled time sale stocks" }
        }
    }
}
