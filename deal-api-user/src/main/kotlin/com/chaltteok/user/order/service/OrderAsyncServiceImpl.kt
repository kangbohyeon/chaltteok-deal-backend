package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.user.infrastructure.kafka.OrderEventProducer
import com.chaltteok.user.order.dto.AsyncOrderResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.enums.OrderErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class OrderAsyncServiceImpl(
    private val dailyStockRepository: DailyStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val orderEventProducer: OrderEventProducer,
) : OrderAsyncService {

    @Transactional(readOnly = true)
    override fun placeOrderAsync(userId: Long, request: OrderRequest): AsyncOrderResponse {
        val dailyStock = dailyStockRepository.findByStockUuid(request.stockUuid)
            ?: throw BusinessException(OrderErrorCode.DAILY_STOCK_NOT_FOUND)

        if (dailyStock.status != DailyStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }
        if (dailyStock.remainStock < request.quantity) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val maxPurchaseCount = dailyStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val participated = eventHistoryRepository.countByUser_IdAndDailyStock_Id(userId, dailyStock.id)
            if (participated + request.quantity > maxPurchaseCount) {
                // participated == 0: 구매 이력은 없지만 요청 수량 자체가 1인 한도 초과
                // participated  > 0: 이미 참여한 이력이 있어 추가 구매 불가
                if (participated == 0L) throw BusinessException(OrderErrorCode.EXCEEDS_MAX_PURCHASE_COUNT)
                throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
            }
        }

        val dailyStockId = dailyStock.id ?: error("DailyStock ID가 null입니다")
        orderEventProducer.sendOrderEvent(userId, dailyStockId)
        logger.info { "타임세일 주문 이벤트 발행 — stockUuid=${request.stockUuid}, userId=$userId" }

        return AsyncOrderResponse.pending()
    }
}
