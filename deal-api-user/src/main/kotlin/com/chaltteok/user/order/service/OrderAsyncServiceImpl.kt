package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.user.infrastructure.kafka.OrderEventProducer
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.enums.OrderErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class OrderAsyncServiceImpl(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val orderEventProducer: OrderEventProducer,
) : OrderAsyncService {

    @Transactional(readOnly = true)
    override fun placeOrderAsync(userId: Long, request: OrderRequest): OrderResponse {
        val timeSaleStock = timeSaleStockRepository.findByStockUuid(request.stockUuid)
            ?: throw BusinessException(OrderErrorCode.TIME_SALE_STOCK_NOT_FOUND)

        if (timeSaleStock.status != TimeSaleStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }
        if (timeSaleStock.remainStock < request.quantity) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val maxPurchaseCount = timeSaleStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val participated = eventHistoryRepository.countByUser_IdAndTimeSaleStock_Id(userId, timeSaleStock.id)
            if (participated + request.quantity > maxPurchaseCount) {
                if (participated >= maxPurchaseCount) throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
                throw BusinessException(OrderErrorCode.EXCEEDS_MAX_PURCHASE_COUNT)
            }
        }

        val timeSaleStockId = timeSaleStock.id ?: error("TimeSaleStock ID가 null입니다")
        orderEventProducer.sendOrderEvent(userId, timeSaleStockId, request.quantity, request.paymentMethod)
        logger.info { "타임세일 비동기 주문 이벤트 발행 — stockUuid=${request.stockUuid}, userId=$userId" }

        return OrderResponse.pending()
    }
}
