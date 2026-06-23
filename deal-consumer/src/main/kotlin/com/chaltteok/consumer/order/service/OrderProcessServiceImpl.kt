package com.chaltteok.consumer.order.service

import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.consumer.order.service.helper.EventHistoryDuplicateChecker
import com.chaltteok.consumer.order.service.helper.StockDecrementHelper
import com.chaltteok.core.infrastructure.lock.DistributedLockService
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import com.chaltteok.core.repository.user.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

private const val MAX_RETRY = 3
private const val RETRY_DELAY_MS = 50L
// 분산 락 leaseTime: 재시도 최대 시간(150ms) + DB 처리 여유분을 합산하여 5s로 설정
private const val LOCK_LEASE_SEC = 5L

@Service
class OrderProcessServiceImpl(
    private val userRepository: UserRepository,
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val duplicateChecker: EventHistoryDuplicateChecker,
    private val stockDecrementHelper: StockDecrementHelper,
    private val orderConfirmService: OrderConfirmService,
    private val distributedLockService: DistributedLockService,
) : OrderProcessService {

    override fun processOrder(command: OrderProcessCommand) {
        require(command.quantity >= 1) { "유효하지 않은 quantity: ${command.quantity}" }

        val user = userRepository.findById(command.userId)
            .orElseThrow { OrderProcessingException("User not found: ${command.userId}") }
        val timeSaleStock = timeSaleStockRepository.findById(command.timeSaleStockId)
            .orElseThrow { OrderProcessingException("TimeSaleStock not found: ${command.timeSaleStockId}") }

        if (duplicateChecker.isDuplicate(user, timeSaleStock)) {
            log.warn { "중복 구매 요청 무시 — userId=${command.userId}, timeSaleStockId=${command.timeSaleStockId}" }
            return
        }

        val lockKey = "lock:time-sale-stock:${command.timeSaleStockId}"
        // waitSec=0: Kafka 컨슈머 스레드 블로킹 방지 — 락 획득 실패 시 즉시 스킵, Kafka 재처리에 위임
        distributedLockService.withLock(
            key = lockKey,
            waitSec = 0L,
            leaseSec = LOCK_LEASE_SEC,
            onFail = {
                log.warn { "분산 락 획득 실패로 주문 처리 스킵 — userId=${command.userId}, timeSaleStockId=${command.timeSaleStockId}" }
            },
        ) {
            var retries = 0
            var stockDecremented = false
            while (!stockDecremented && retries < MAX_RETRY) {
                try {
                    stockDecremented = stockDecrementHelper.tryDecrement(command.timeSaleStockId, command.quantity)
                    if (!stockDecremented) {
                        log.warn { "재고 소진 — timeSaleStockId=${command.timeSaleStockId}" }
                        return@withLock
                    }
                } catch (e: ObjectOptimisticLockingFailureException) {
                    retries++
                    log.warn { "낙관적 락 충돌, 재시도 $retries/$MAX_RETRY — timeSaleStockId=${command.timeSaleStockId}" }
                    if (retries >= MAX_RETRY) {
                        log.error { "낙관적 락 재시도 초과, 주문 처리 포기 — timeSaleStockId=${command.timeSaleStockId}" }
                        return@withLock
                    }
                    Thread.sleep(RETRY_DELAY_MS)
                }
            }
            // 별도 빈을 통해 호출 → Spring 프록시 경유 → @Transactional 적용
            orderConfirmService.confirmOrder(user, timeSaleStock, command.quantity, command.paymentMethod, command.couponCode)
        }
    }
}
