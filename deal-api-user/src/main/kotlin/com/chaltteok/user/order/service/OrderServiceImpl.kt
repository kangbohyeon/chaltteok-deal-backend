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
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.enums.OrderErrorCode
import com.chaltteok.core.service.orderstats.OrderStatsService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}
private val ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

@Service
class OrderServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val notificationRepository: NotificationRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderStatsService: OrderStatsService,
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
        if (dailyStock.remainStock < request.quantity) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val maxPurchaseCount = dailyStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val participationCount = eventHistoryRepository.countByUser_IdAndDailyStock_Id(userId, dailyStock.id)
            if (participationCount + request.quantity > maxPurchaseCount) {
                if (participationCount == 0L) {
                    throw BusinessException(OrderErrorCode.EXCEEDS_MAX_PURCHASE_COUNT)
                }
                throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
            }
        }

        dailyStock.decrease(request.quantity)

        val totalPrice = dailyStock.salePrice * request.quantity
        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = request.quantity, price = dailyStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice, status = PaymentStatus.SUCCESS, paymentMethod = "TIMESALE")
        )
        eventHistoryRepository.save(EventHistory(user = user, dailyStock = dailyStock, order = order))

        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "${dailyStock.product.name} (%,d원)".format(dailyStock.salePrice),
            )
        )

        logger.info { "타임세일 주문 완료 — stockUuid=${request.stockUuid}, orderNumber=${order.orderNumber}" }

        val orderId = order.id ?: error("Order ID가 저장 후에도 null입니다")
        applicationEventPublisher.publishEvent(
            OrderCompletedEvent(
                orderId = orderId,
                orderNumber = order.orderNumber,
                userEmail = user.email,
                userName = user.nickname,
                productName = dailyStock.product.name,
                totalAmount = totalPrice.toLong(),
                orderedAt = order.orderedAt.format(ORDER_DATE_FORMATTER),
            )
        )
        orderStatsService.incrementOrderStats(
            date = LocalDate.now(),
            revenue = totalPrice.toLong(),
        )
        return CheckoutResponse(
            orderId = orderId,
            totalAmount = totalPrice.toLong(),
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
        orderStatsService.incrementCancelStats(date = LocalDate.now())

        val orderIds = listOfNotNull(order.id)
        paymentRepository.findByOrderIds(orderIds).forEach { it.cancel() }
    }
}
