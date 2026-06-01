package com.chaltteok.owner.product.scheduler

import com.chaltteok.core.repository.product.ProductRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Component
class ProductStockScheduler(
    private val productRepository: ProductRepository,
) {
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    fun resetDailyStock() {
        val products = productRepository.findAllByStockQuantityIsNotNull()
        products.forEach { product ->
            product.currentStock = product.stockQuantity
            if ((product.stockQuantity ?: 0) > 0) {
                product.isSoldOut = false
            }
        }
        logger.info { "Daily stock reset completed: ${products.size} products" }
    }
}
