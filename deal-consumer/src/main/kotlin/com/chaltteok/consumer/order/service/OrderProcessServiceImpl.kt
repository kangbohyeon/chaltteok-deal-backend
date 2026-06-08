package com.chaltteok.consumer.order.service

import com.chaltteok.consumer.order.service.helper.EventHistoryDuplicateChecker
import com.chaltteok.consumer.order.service.helper.StockDecrementHelper
import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.User
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
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

private const val MAX_RETRY = 3
private const val RETRY_DELAY_MS = 50L
private const val PAYMENT_METHOD_TIMESALE = "TIMESALE"

@Service
class OrderProcessServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val notificationRepository: NotificationRepository,
    private val duplicateChecker: EventHistoryDuplicateChecker,
    private val stockDecrementHelper: StockDecrementHelper,
) : OrderProcessService {

    override fun processOrder(userId: Long, dailyStockId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found: $userId") }
        val dailyStock = dailyStockRepository.findById(dailyStockId)
            .orElseThrow { RuntimeException("DailyStock not found: $dailyStockId") }

        if (duplicateChecker.isDuplicate(user, dailyStock)) {
            log.warn { "중복 구매 요청 무시 — userId=$userId, dailyStockId=$dailyStockId" }
            return
        }

        var retries = 0
        var stockDecremented = false
        while (!stockDecremented && retries < MAX_RETRY) {
            try {
                stockDecremented = stockDecrementHelper.tryDecrement(dailyStockId)
                if (!stockDecremented) {
                    log.warn { "재고 소진 — dailyStockId=$dailyStockId" }
                    return
                }
            } catch (e: ObjectOptimisticLockingFailureException) {
                retries++
                log.warn { "낙관적 락 충돌, 재시도 $retries/$MAX_RETRY — dailyStockId=$dailyStockId" }
                if (retries >= MAX_RETRY) {
                    log.error { "낙관적 락 재시도 초과, 주문 처리 포기 — dailyStockId=$dailyStockId" }
                    return
                }
                Thread.sleep(RETRY_DELAY_MS)
            }
        }

        confirmOrder(user, dailyStock)
    }

    @Transactional
    internal fun confirmOrder(user: User, dailyStock: DailyStock) {
        val totalPrice = dailyStock.salePrice

        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = 1, price = dailyStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice, status = PaymentStatus.SUCCESS, paymentMethod = PAYMENT_METHOD_TIMESALE)
        )

        eventHistoryRepository.findByUserAndDailyStock(user, dailyStock)?.let {
            it.order = order
        }

        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "${dailyStock.product.name} (%,d원)".format(dailyStock.salePrice),
            )
        )

        log.info { "주문 확정 완료 — orderId=${order.id}, userId=${user.id}, dailyStockId=${dailyStock.id}" }
    }
}
