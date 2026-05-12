package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.infrastructure.kafka.OrderEventProducer
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.enums.OrderErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val orderEventProducer: OrderEventProducer,
) : OrderService {

    @Transactional(readOnly = true)
    override fun placeOrder(userId: Long, request: OrderRequest) {
        userRepository.findById(userId)
            .orElseThrow { BusinessException(OrderErrorCode.USER_NOT_FOUND) }

        val dailyStock = dailyStockRepository.findById(request.dailyStockId)
            .orElseThrow { BusinessException(OrderErrorCode.DAILY_STOCK_NOT_FOUND) }

        if (dailyStock.status != DailyStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }

        if (eventHistoryRepository.existsByUser_IdAndDailyStock_Id(userId, dailyStock.id)) {
            throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
        }

        orderEventProducer.sendOrderEvent(userId, dailyStock.id!!)
    }
}
