package com.chaltteok.owner.dailystock.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.dto.OwnerDailyStockListResponse
import com.chaltteok.owner.dailystock.enums.DailyStockErrorCode
import com.chaltteok.owner.dailystock.enums.DailyStockType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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

        val initialStatus = if (stockType == DailyStockType.TIMESALE) {
            val now = LocalDateTime.now()
            if (dailyStocksRegisterRequest.startAt != null && dailyStocksRegisterRequest.startAt.isAfter(now)) {
                DailyStockStatus.SCHEDULED
            } else {
                DailyStockStatus.OPEN
            }
        } else {
            DailyStockStatus.OPEN
        }

        val stock = dailyStocksRegisterRequest.toDailyStockEntity(productOption.product, finalPrice, initialStatus)

        try {
            dailyStockRepository.save(stock)
            logger.info { "daily stock registered successfully" }
        } catch (e: DataIntegrityViolationException) {
            logger.warn { "Duplicate stock registration attempt" }
            throw BusinessException(DailyStockErrorCode.DUPLICATE_STOCK)
        }
    }

    @Transactional(readOnly = true)
    override fun findAllDailyStocks(): List<OwnerDailyStockListResponse> {
        val stocks = dailyStockRepository.findAllWithProduct()
        val optionMap = productOptionRepository
            .findAllByProductIn(stocks.map { it.product }.distinctBy { it.id })
            .associateBy { it.product.id }
        return stocks.map { stock ->
            val optionUuid = optionMap[stock.product.id]?.optionUuid ?: ""
            OwnerDailyStockListResponse.from(stock, optionUuid)
        }
    }

    @Transactional
    override fun deleteDailyStock(stockUuid: String) {
        val stock = dailyStockRepository.findByStockUuid(stockUuid)
            ?: throw BusinessException(DailyStockErrorCode.INVALID_ID)
        dailyStockRepository.delete(stock)
        logger.info { "daily stock deleted: $stockUuid" }
    }

    @Transactional
    override fun updateDailyStock(stockUuid: String, request: DailyStocksRegisterRequest) {
        val existing = dailyStockRepository.findByStockUuid(stockUuid)
            ?: throw BusinessException(DailyStockErrorCode.INVALID_ID)
        val productOption = productOptionRepository.findProductOptionByOptionUuid(request.optionId)
            .orElseThrow { BusinessException(DailyStockErrorCode.INVALID_OPTION_ID) }
        val finalPrice = request.salePrice ?: productOption.price
        existing.update(finalPrice, request.totalQty, request.startAt, request.endAt, request.maxPurchaseCount)
        logger.info { "daily stock updated: $stockUuid" }
    }
}