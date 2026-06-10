package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.User
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
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
    private val outboxEventWriter: OutboxEventWriter,
) {
    @Transactional
    fun confirmOrder(user: User, dailyStock: DailyStock, paymentMethod: String) {
        val totalPrice = dailyStock.salePrice

        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = 1, price = dailyStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice, status = PaymentStatus.SUCCESS, paymentMethod = paymentMethod, paidAt = LocalDateTime.now())
        )

        // order 미설정인 EventHistory(DuplicateChecker가 생성)에 order 역참조 backfill
        eventHistoryRepository.findFirstByUserAndDailyStockAndOrderIsNull(user, dailyStock)?.let {
            it.order = order
        }

        outboxEventWriter.write(
            source = OutboxEvent.SOURCE_CONSUMER,
            aggregateId = order.orderNumber,
            eventType = OutboxEvent.TYPE_ORDER_COMPLETED,
            event = OrderCompletedEvent(
                orderId = order.id ?: error("Order ID null"),
                orderNumber = order.orderNumber,
                userName = user.nickname,
                productName = dailyStock.product.name,
                totalAmount = totalPrice.toLong(),
                orderedAt = order.orderedAt,
            )
        )

        log.info { "주문 확정 완료 — orderId=${order.id}, userId=${user.id}, dailyStockId=${dailyStock.id}" }
    }
}
