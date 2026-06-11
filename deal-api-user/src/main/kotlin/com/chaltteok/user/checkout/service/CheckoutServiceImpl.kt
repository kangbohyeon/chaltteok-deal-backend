package com.chaltteok.user.checkout.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.infrastructure.lock.DistributedLockService
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.checkout.enums.CheckoutErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class CheckoutServiceImpl(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val outboxEventWriter: OutboxEventWriter,
    private val distributedLockService: DistributedLockService,
) : CheckoutService {

    @Transactional
    override fun checkout(userId: Long, request: CheckoutRequest): CheckoutResponse {
        if (request.items.isEmpty()) throw BusinessException(CheckoutErrorCode.EMPTY_CART)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(CheckoutErrorCode.USER_NOT_FOUND) }

        val productUuids = request.items.map { it.productUuid }
        val lockKeys = productUuids.map { "lock:product:$it" }

        return distributedLockService.withMultiLock(
            keys = lockKeys,
            onFail = { throw BusinessException(CheckoutErrorCode.STOCK_LOCK_FAILED) },
        ) {
            val productMap = productRepository.findAllByProductUuidInWithLock(productUuids)
                .associateBy { it.productUuid }

            // 서버 측 가격으로 총액 계산 및 재고 차감 (클라이언트 전달 금액 미신뢰)
            val serverTotal = request.items.sumOf { item ->
                val product = productMap[item.productUuid]
                    ?: throw BusinessException(CheckoutErrorCode.PRODUCT_NOT_FOUND)
                if (product.currentStock != null) {
                    if (product.currentStock!! < item.quantity) {
                        throw BusinessException(CheckoutErrorCode.INSUFFICIENT_STOCK)
                    }
                    product.currentStock = product.currentStock!! - item.quantity
                    if (product.currentStock == 0) product.isSoldOut = true
                }
                product.price.toLong() * item.quantity
            }

            val order = Order(user = user, totalPrice = serverTotal.toInt(), status = OrderStatus.PENDING)
            val savedOrder = orderRepository.save(order)

            val orderItems = request.items.map { item ->
                val product = productMap[item.productUuid]!!
                OrderItem(order = savedOrder, product = product, quantity = item.quantity, price = product.price)
            }
            orderItemRepository.saveAll(orderItems)

            paymentRepository.save(
                Payment(
                    order = savedOrder,
                    amount = serverTotal.toInt(),
                    status = PaymentStatus.SUCCESS,
                    paymentMethod = request.paymentMethod.name,
                    paidAt = LocalDateTime.now(),
                )
            )

            savedOrder.status = OrderStatus.COMPLETED

            val orderId = savedOrder.id ?: error("Order ID가 저장 후에도 null입니다")
            val productName = buildString {
                for ((index, item) in orderItems.withIndex()) {
                    if (index > 0) append(", ")
                    if (length + item.product.name.length > 450) {
                        append("…"); break
                    }
                    append(item.product.name)
                }
            }

            outboxEventWriter.write(
                source = OutboxEvent.SOURCE_API_USER,
                aggregateId = savedOrder.orderNumber,
                eventType = OutboxEvent.TYPE_ORDER_COMPLETED,
                event = OrderCompletedEvent(
                    orderId = orderId,
                    orderNumber = savedOrder.orderNumber,
                    userName = user.nickname,
                    productName = productName,
                    totalAmount = serverTotal,
                    orderedAt = savedOrder.orderedAt,
                )
            )

            logger.info { "일반 주문 완료 — orderNumber=${savedOrder.orderNumber}" }

            CheckoutResponse(
                orderId = orderId,
                totalAmount = savedOrder.totalPrice.toLong(),
                status = savedOrder.status.name,
            )
        }
    }
}
