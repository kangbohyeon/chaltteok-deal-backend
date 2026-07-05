package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.User
import com.chaltteok.core.domain.enums.NotificationType
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentMethod
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
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

        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "${order.orderNumber} (%,d원)".format(totalPrice),
                orderNumber = order.orderNumber,
            )
        )

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
