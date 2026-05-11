package com.chaltteok.consumer.order.service

import com.chaltteok.consumer.order.service.helper.EventHistoryDuplicateChecker
import com.chaltteok.consumer.order.service.helper.StockDecrementHelper
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OrderStatus
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.user.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

private const val MAX_RETRY = 3
private const val RETRY_DELAY_MS = 50L

@Service
class OrderProcessServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val duplicateChecker: EventHistoryDuplicateChecker,
    private val stockDecrementHelper: StockDecrementHelper,
) : OrderProcessService {

    override fun processOrder(userId: Long, dailyStockId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found: $userId") }
        val dailyStock = dailyStockRepository.findById(dailyStockId)
            .orElseThrow { RuntimeException("DailyStock not found: $dailyStockId") }

        // 1단계: 1인 1회 구매 검증 (REQUIRES_NEW 트랜잭션 — UniqueKey 위반 격리)
        if (duplicateChecker.isDuplicate(user, dailyStock)) {
            log.warn { "중복 구매 요청 무시 — userId=$userId, dailyStockId=$dailyStockId" }
            return
        }

        // 2단계: 재고 차감 (낙관적 락 재시도, 각 시도마다 별도 REQUIRES_NEW 트랜잭션)
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

        // 3단계: 주문 확정 (Order + OrderItem 저장, EventHistory에 order 역참조 backfill)
        confirmOrder(userId, dailyStockId)
    }

    @Transactional
    fun confirmOrder(userId: Long, dailyStockId: Long) {
        val user = userRepository.findById(userId).orElseThrow()
        val dailyStock = dailyStockRepository.findById(dailyStockId).orElseThrow()

        val order = orderRepository.save(
            Order(user = user, totalPrice = dailyStock.product.price, status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = dailyStock.product, quantity = 1, price = dailyStock.product.price)
        )

        // EventHistory에 확정된 Order 역참조 backfill
        eventHistoryRepository.findByUserAndDailyStock(user, dailyStock)?.let {
            it.order = order
        }

        log.info { "주문 확정 완료 — orderId=${order.id}, userId=$userId, dailyStockId=$dailyStockId" }
    }
}
