package com.chaltteok.user.checkout.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.checkout.enums.CheckoutErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}
private val ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

@Service
class CheckoutServiceImpl(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : CheckoutService {

    @Transactional
    override fun checkout(userId: Long, request: CheckoutRequest): CheckoutResponse {
        if (request.items.isEmpty()) throw BusinessException(CheckoutErrorCode.EMPTY_CART)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(CheckoutErrorCode.USER_NOT_FOUND) }

        val productUuids = request.items.map { it.productUuid }
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
                paymentMethod = request.paymentMethod,
                paidAt = LocalDateTime.now(),
            )
        )

        savedOrder.status = OrderStatus.COMPLETED

        val orderId = savedOrder.id ?: error("Order ID가 저장 후에도 null입니다")
        val productName = orderItems.joinToString(", ") { it.product.name }
            .let { if (it.length > 450) it.take(450) + "…" else it }

        // 트랜잭션 커밋 후 이메일·알림·통계를 각 EventListener가 처리
        applicationEventPublisher.publishEvent(
            OrderCompletedEvent(
                orderId = orderId,
                orderNumber = savedOrder.orderNumber,
                userName = user.nickname,
                productName = productName,
                totalAmount = serverTotal,
                orderedAt = savedOrder.orderedAt.format(ORDER_DATE_FORMATTER),
            )
        )

        logger.info { "일반 주문 완료 — orderNumber=${savedOrder.orderNumber}" }

        return CheckoutResponse(
            orderId = orderId,
            totalAmount = savedOrder.totalPrice.toLong(),
            status = savedOrder.status.name,
        )
    }
}
