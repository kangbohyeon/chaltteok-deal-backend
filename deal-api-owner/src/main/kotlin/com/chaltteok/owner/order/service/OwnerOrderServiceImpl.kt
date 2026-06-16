package com.chaltteok.owner.order.service

import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class OwnerOrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
) : OwnerOrderService {

    @Transactional(readOnly = true)
    override fun getOrderDetail(orderNumber: String): OwnerOrderDetailResponse {
        val order = orderRepository.findByOrderNumber(orderNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다: $orderNumber")
        val items = orderItemRepository.findByOrderIdWithProduct(order.id!!)
        val payment = paymentRepository.findByOrderId(order.id!!)
        return OwnerOrderDetailResponse.from(order, items, payment)
    }
}
