package com.chaltteok.user.checkout.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.enums.NotificationType
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.checkout.enums.CheckoutErrorCode
import com.chaltteok.core.service.orderstats.OrderStatsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CheckoutServiceImpl(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val notificationRepository: NotificationRepository,
    private val orderStatsService: OrderStatsService,
) : CheckoutService {

    @Transactional
    override fun checkout(userId: Long, request: CheckoutRequest): CheckoutResponse {
        if (request.items.isEmpty()) throw BusinessException(CheckoutErrorCode.EMPTY_CART)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(CheckoutErrorCode.USER_NOT_FOUND) }

        val order = Order(user = user, totalPrice = request.totalAmount.toInt(), status = OrderStatus.PENDING)
        val savedOrder = orderRepository.save(order)

        val productUuids = request.items.map { it.productUuid }
        val productMap = productRepository.findAllByProductUuidInWithLock(productUuids)
            .associateBy { it.productUuid }

        val orderItems = request.items.map { item ->
            val product = productMap[item.productUuid]
                ?: throw BusinessException(CheckoutErrorCode.PRODUCT_NOT_FOUND)
            if (product.currentStock != null) {
                if (product.currentStock!! < item.quantity) {
                    throw BusinessException(CheckoutErrorCode.INSUFFICIENT_STOCK)
                }
                product.currentStock = product.currentStock!! - item.quantity
                if (product.currentStock == 0) product.isSoldOut = true
            }
            OrderItem(order = savedOrder, product = product, quantity = item.quantity, price = item.price.toInt())
        }
        orderItemRepository.saveAll(orderItems)

        val payment = Payment(order = savedOrder, amount = request.totalAmount.toInt(), status = PaymentStatus.SUCCESS, paymentMethod = request.paymentMethod)
        paymentRepository.save(payment)

        savedOrder.status = OrderStatus.COMPLETED

        orderStatsService.incrementOrderStats(
            date = LocalDate.now(),
            revenue = request.totalAmount,
        )

        val productNames = orderItems.joinToString(", ") { it.product.name }
            .let { if (it.length > 450) it.take(450) + "…" else it }
        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "$productNames (%,d원)".format(request.totalAmount),
            )
        )

        return CheckoutResponse(
            orderId = savedOrder.id!!,
            totalAmount = savedOrder.totalPrice.toLong(),
            status = savedOrder.status.name,
        )
    }
}
