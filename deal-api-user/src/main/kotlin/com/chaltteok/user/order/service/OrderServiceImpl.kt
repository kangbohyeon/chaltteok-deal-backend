package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.domain.enums.NotificationType
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.enums.OrderErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class OrderServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val notificationRepository: NotificationRepository,
) : OrderService {

    @Transactional
    override fun placeOrder(userId: Long, request: OrderRequest): CheckoutResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(OrderErrorCode.USER_NOT_FOUND) }

        val dailyStock = dailyStockRepository.findByStockUuidWithLock(request.stockUuid)
            ?: throw BusinessException(OrderErrorCode.DAILY_STOCK_NOT_FOUND)

        if (dailyStock.status != DailyStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }
        if (dailyStock.remainStock <= 0) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val participationCount = eventHistoryRepository.countByUser_IdAndDailyStock_Id(userId, dailyStock.id)
        if (participationCount >= dailyStock.maxPurchaseCount) {
            throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
        }

        dailyStock.remainStock -= 1
        if (dailyStock.remainStock == 0) dailyStock.status = DailyStockStatus.SOLD_OUT

        val order = orderRepository.save(
            Order(user = user, totalPrice = dailyStock.salePrice, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = 1, price = dailyStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = dailyStock.salePrice, status = PaymentStatus.SUCCESS, paymentMethod = "TIMESALE")
        )

        try {
            eventHistoryRepository.saveAndFlush(EventHistory(user = user, dailyStock = dailyStock, order = order))
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
        }

        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "${dailyStock.product.name} (%,d원)".format(dailyStock.salePrice),
            )
        )

        logger.info { "타임세일 주문 완료 — userId=$userId, stockUuid=${request.stockUuid}, orderId=${order.id}" }

        return CheckoutResponse(
            orderId = order.id!!,
            totalAmount = dailyStock.salePrice.toLong(),
            status = order.status.name,
        )
    }

    @Transactional
    override fun cancelOrder(userId: Long, orderNumber: String) {
        val order = orderRepository.findByOrderNumberAndUser_Id(orderNumber, userId)
            .orElseThrow { BusinessException(OrderErrorCode.ORDER_NOT_FOUND) }

        if (order.status == OrderStatus.CANCELLED) {
            throw BusinessException(OrderErrorCode.ORDER_ALREADY_CANCELLED)
        }
        if (!order.isCancellable()) {
            throw BusinessException(OrderErrorCode.ORDER_NOT_CANCELLABLE)
        }

        order.cancel()

        val orderIds = listOfNotNull(order.id)
        paymentRepository.findByOrderIds(orderIds).forEach { it.cancel() }
    }
}
