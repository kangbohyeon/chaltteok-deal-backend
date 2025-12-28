package com.chaltteok.owner.dailystock.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.enums.DailyStockErrorCode
import com.chaltteok.owner.dailystock.enums.DailyStockType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class DailyStockServiceImpl(
    private val dailyStockRepository: DailyStockRepository,
    private val productOptionRepository: ProductOptionRepository,
) : DailyStockService {
    override fun registerDailyStock(dailyStocksRegisterRequest: DailyStocksRegisterRequest) {
        val productOption =
            productOptionRepository.findProductOptionByOptionUuid(dailyStocksRegisterRequest.optionId)
                .orElseThrow { BusinessException(DailyStockErrorCode.INVALID_ID) }
        logger.info { "product option select success" }

        val finalPrice = dailyStocksRegisterRequest.salePrice ?: productOption.price
        val stockType = dailyStocksRegisterRequest.stockType ?: DailyStockType.NORMAL
        if (stockType == DailyStockType.EVENT && finalPrice == 0) {
            logger.warn { "Event stock must have price > 0" }
            throw BusinessException(DailyStockErrorCode.EVENT_PRICE_REQUIRED)
        }

        val stock = dailyStocksRegisterRequest.toDailyStockEntity(productOption, finalPrice)

        try {
            dailyStockRepository.save(stock)
            logger.info { "daily stock registered successfully" }
        } catch (e: DataIntegrityViolationException) {
            logger.warn { "Duplicate stock registration attempt" }
            throw BusinessException(DailyStockErrorCode.DUPLICATE_STOCK)
        }
    }
}