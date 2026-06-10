package com.chaltteok.consumer.order.service

import com.chaltteok.consumer.order.service.helper.EventHistoryDuplicateChecker
import com.chaltteok.consumer.order.service.helper.StockDecrementHelper
import com.chaltteok.core.domain.enums.PaymentMethod
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.user.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

private const val MAX_RETRY = 3
private const val RETRY_DELAY_MS = 50L

@Service
class OrderProcessServiceImpl(
    private val userRepository: UserRepository,
    private val dailyStockRepository: DailyStockRepository,
    private val duplicateChecker: EventHistoryDuplicateChecker,
    private val stockDecrementHelper: StockDecrementHelper,
    private val orderConfirmService: OrderConfirmService,
) : OrderProcessService {

    override fun processOrder(userId: Long, dailyStockId: Long, paymentMethod: PaymentMethod) {
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

        // 별도 빈을 통해 호출 → Spring 프록시 경유 → @Transactional 적용
        orderConfirmService.confirmOrder(user, dailyStock, paymentMethod)
    }
}
