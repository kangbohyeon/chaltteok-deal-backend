package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.User
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentMethod
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class OrderConfirmService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val notificationRepository: NotificationRepository,
    private val outboxEventWriter: OutboxEventWriter,
) {
    @Transactional
    fun confirmOrder(user: User, timeSaleStock: TimeSaleStock, quantity: Int, paymentMethod: PaymentMethod) {
        // 분산락 내부 이중 검증 — lock-before-check 이후 커밋된 EventHistory까지 포함하여 재산정
        val maxPurchaseCount = timeSaleStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val userId = user.id ?: throw OrderProcessingException("User ID null — userId=${user.id}")
            val participated = eventHistoryRepository.countByUser_IdAndTimeSaleStock_Id(userId, timeSaleStock.id)
            if (participated + quantity > maxPurchaseCount) {
                log.warn { "구매 한도 초과(double-check) — userId=$userId, timeSaleStockId=${timeSaleStock.id}, participated=$participated, requested=$quantity, max=$maxPurchaseCount" }
                throw OrderProcessingException("구매 한도 초과: userId=$userId, timeSaleStockId=${timeSaleStock.id}")
            }
        }

        val totalPrice = timeSaleStock.salePrice.toLong() * quantity

        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice.toInt(), status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = timeSaleStock.product, quantity = quantity, price = timeSaleStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice.toInt(), status = PaymentStatus.SUCCESS, paymentMethod = paymentMethod.name, paidAt = LocalDateTime.now())
        )

        eventHistoryRepository.save(
            EventHistory(user = user, timeSaleStock = timeSaleStock, order = order)
        )

        notificationRepository.save(Notification.forOrder(order.orderNumber, totalPrice))

        outboxEventWriter.write(
            source = OutboxEvent.SOURCE_CONSUMER,
            aggregateId = order.orderNumber,
            eventType = OutboxEvent.TYPE_ORDER_COMPLETED,
            event = OrderCompletedEvent(
                orderId = order.id ?: error("Order ID null"),
                orderNumber = order.orderNumber,
                userName = user.nickname,
                productName = timeSaleStock.product.name,
                totalAmount = totalPrice.toLong(),
                orderedAt = order.orderedAt,
            )
        )

        log.info { "주문 확정 완료 — orderId=${order.id}, userId=${user.id}, timeSaleStockId=${timeSaleStock.id}" }
    }
}
