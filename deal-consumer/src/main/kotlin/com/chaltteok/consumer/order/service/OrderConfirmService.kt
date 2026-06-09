package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.User
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}
private val ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private const val PAYMENT_METHOD_TIMESALE = "TIMESALE"

@Service
class OrderConfirmService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun confirmOrder(user: User, dailyStock: DailyStock) {
        val totalPrice = dailyStock.salePrice

        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = 1, price = dailyStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice, status = PaymentStatus.SUCCESS, paymentMethod = PAYMENT_METHOD_TIMESALE, paidAt = LocalDateTime.now())
        )

        // order 미설정인 EventHistory(DuplicateChecker가 생성)에 order 역참조 backfill
        eventHistoryRepository.findFirstByUserAndDailyStockAndOrderIsNull(user, dailyStock)?.let {
            it.order = order
        }

        // 트랜잭션 커밋 후 이메일·알림·통계를 각 EventListener가 처리
        applicationEventPublisher.publishEvent(
            OrderCompletedEvent(
                orderId = order.id ?: error("Order ID null"),
                orderNumber = order.orderNumber,
                userName = user.nickname,
                productName = dailyStock.product.name,
                totalAmount = totalPrice.toLong(),
                orderedAt = order.orderedAt.format(ORDER_DATE_FORMATTER),
            )
        )

        log.info { "주문 확정 완료 — orderId=${order.id}, userId=${user.id}, dailyStockId=${dailyStock.id}" }
    }
}
