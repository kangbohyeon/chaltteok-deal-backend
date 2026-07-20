package com.chaltteok.owner.timesalestock.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.timesalestock.dto.OwnerTimeSaleStockListResponse
import com.chaltteok.owner.timesalestock.dto.TimeSaleStocksRegisterRequest
import com.chaltteok.owner.timesalestock.enums.TimeSaleStockErrorCode
import com.chaltteok.owner.timesalestock.enums.TimeSaleStockType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class TimeSaleStockServiceImpl(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val eventHistoryRepository: EventHistoryRepository,
) : TimeSaleStockService {
    @Transactional
    override fun registerTimeSaleStock(request: TimeSaleStocksRegisterRequest) {
        val productOption =
            productOptionRepository.findProductOptionByOptionUuid(request.optionId)
                .orElseThrow { BusinessException(TimeSaleStockErrorCode.INVALID_ID) }
        logger.info { "product option select success" }

        val finalPrice = request.salePrice ?: productOption.price
        val stockType = request.stockType ?: TimeSaleStockType.NORMAL
        if (stockType == TimeSaleStockType.EVENT && finalPrice == 0) {
            logger.warn { "Event stock must have price > 0" }
            throw BusinessException(TimeSaleStockErrorCode.EVENT_PRICE_REQUIRED)
        }

        val initialStatus = if (stockType == TimeSaleStockType.TIMESALE) {
            val now = LocalDateTime.now()
            if (request.startAt != null && request.startAt.isAfter(now)) {
                TimeSaleStockStatus.SCHEDULED
            } else {
                TimeSaleStockStatus.OPEN
            }
        } else {
            TimeSaleStockStatus.OPEN
        }

        val stock = request.toTimeSaleStockEntity(productOption.product, finalPrice, initialStatus)

        try {
            timeSaleStockRepository.save(stock)
            logger.info { "time sale stock registered successfully" }
        } catch (e: DataIntegrityViolationException) {
            logger.warn { "Duplicate stock registration attempt" }
            throw BusinessException(TimeSaleStockErrorCode.DUPLICATE_STOCK)
        }
    }

    @Transactional(readOnly = true)
    override fun findAllTimeSaleStocks(): List<OwnerTimeSaleStockListResponse> {
        val stocks = timeSaleStockRepository.findAllWithProduct()
        val optionMap = productOptionRepository
            .findAllByProductIn(stocks.map { it.product }.distinctBy { it.id })
            .associateBy { it.product.id }
        return stocks.map { stock ->
            val optionUuid = optionMap[stock.product.id]?.optionUuid ?: ""
            OwnerTimeSaleStockListResponse.from(stock, optionUuid)
        }
    }

    @Transactional(readOnly = true)
    override fun findTimeSaleStock(stockUuid: String): OwnerTimeSaleStockListResponse {
        val stock = timeSaleStockRepository.findByStockUuidWithProduct(stockUuid)
            ?: throw BusinessException(TimeSaleStockErrorCode.INVALID_ID)
        val optionUuid = productOptionRepository.findFirstByProductOrderByIdAsc(stock.product)
            .map { it.optionUuid }.orElse("")
        return OwnerTimeSaleStockListResponse.from(stock, optionUuid)
    }

    @Transactional
    override fun deleteTimeSaleStock(stockUuid: String) {
        val stock = timeSaleStockRepository.findByStockUuid(stockUuid)
            ?: throw BusinessException(TimeSaleStockErrorCode.INVALID_ID)
        eventHistoryRepository.deleteAllByTimeSaleStock(stock)
        timeSaleStockRepository.delete(stock)
        logger.info { "time sale stock deleted: $stockUuid" }
    }

    @Transactional
    override fun updateTimeSaleStock(stockUuid: String, request: TimeSaleStocksRegisterRequest) {
        val existing = timeSaleStockRepository.findByStockUuid(stockUuid)
            ?: throw BusinessException(TimeSaleStockErrorCode.INVALID_ID)
        val productOption = productOptionRepository.findProductOptionByOptionUuid(request.optionId)
            .orElseThrow { BusinessException(TimeSaleStockErrorCode.INVALID_OPTION_ID) }
        val finalPrice = request.salePrice ?: productOption.price
        existing.update(finalPrice, request.totalQty, request.startAt, request.endAt, request.maxPurchaseCount)
        logger.info { "time sale stock updated: $stockUuid" }
    }
}
