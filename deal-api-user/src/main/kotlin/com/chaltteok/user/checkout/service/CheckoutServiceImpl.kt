package com.chaltteok.user.checkout.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.checkout.enums.CheckoutErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutServiceImpl(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
) : CheckoutService {

    @Transactional
    override fun checkout(userId: Long, request: CheckoutRequest): CheckoutResponse {
        if (request.items.isEmpty()) throw BusinessException(CheckoutErrorCode.EMPTY_CART)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(CheckoutErrorCode.USER_NOT_FOUND) }

        val order = Order(user = user, totalPrice = request.totalAmount.toInt(), status = OrderStatus.PENDING)
        val savedOrder = orderRepository.save(order)

        val orderItems = request.items.map { item ->
            val product = productRepository.findById(item.productId)
                .orElseThrow { BusinessException(CheckoutErrorCode.PRODUCT_NOT_FOUND) }
            OrderItem(order = savedOrder, product = product, quantity = item.quantity, price = item.price.toInt())
        }
        orderItemRepository.saveAll(orderItems)

        val payment = Payment(order = savedOrder, amount = request.totalAmount.toInt(), status = PaymentStatus.SUCCESS, paymentMethod = request.paymentMethod)
        paymentRepository.save(payment)

        savedOrder.status = OrderStatus.COMPLETED

        return CheckoutResponse(
            orderId = savedOrder.id!!,
            totalAmount = savedOrder.totalPrice.toLong(),
            status = savedOrder.status.name,
        )
    }
}
