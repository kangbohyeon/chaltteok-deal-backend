package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.user.order.dto.OrderHistoryItemResponse
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.dto.PaymentInfoResponse
import com.chaltteok.user.order.enums.OrderErrorCode
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Service
class OrderQueryServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
) : OrderQueryService {

    @Transactional(readOnly = true)
    override fun getOrderHistory(
        userId: Long,
        keyword: String?,
        status: String?,
        fromDate: String?,
        toDate: String?,
        paymentStatus: String?,
        pageable: Pageable,
    ): OrderHistoryPageResponse {
        val orderStatus = status?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() }
        val from = fromDate?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fromDate format. Expected yyyy-MM-dd")
            }
        }
        val to = toDate?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid toDate format. Expected yyyy-MM-dd")
            }
        }

        val page = orderRepository.findByUserIdPaged(userId, keyword, orderStatus, from, to, paymentStatus, pageable)
        val orders = page.content
        if (orders.isEmpty()) {
            return OrderHistoryPageResponse(
                content = emptyList(),
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = pageable.pageNumber,
                pageSize = pageable.pageSize,
            )
        }

        val orderIds = orders.mapNotNull { it.id }
        val itemsByOrderId = orderItemRepository.findByOrderIdsWithProduct(orderIds)
            .groupBy { it.order?.id }
        val paymentByOrderId = paymentRepository.findByOrderIds(orderIds)
            .associateBy { it.order.id }

        val content = orders.map { order ->
            val items = itemsByOrderId[order.id].orEmpty().map { item ->
                OrderHistoryItemResponse(
                    productName = item.product.name,
                    quantity = item.quantity,
                    price = item.price.toLong(),
                )
            }
            val payment = paymentByOrderId[order.id]?.let { p ->
                PaymentInfoResponse(
                    amount = p.amount,
                    pgProvider = p.pgProvider,
                    paymentMethod = p.paymentMethod,
                    status = p.status.name,
                    paidAt = p.paidAt?.toString(),
                )
            }
            OrderHistoryResponse(
                orderNumber = order.orderNumber,
                totalPrice = order.totalPrice.toLong(),
                status = order.status.name,
                orderedAt = order.orderedAt.toString(),
                items = items,
                payment = payment,
                canCancel = order.isCancellable(),
            )
        }

        return OrderHistoryPageResponse(
            content = content,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            pageSize = page.size,
        )
    }

    @Transactional(readOnly = true)
    override fun getOrderDetail(userId: Long, orderNumber: String): OrderHistoryResponse {
        val order = orderRepository.findByOrderNumberAndUser_Id(orderNumber, userId)
            .orElseThrow { BusinessException(OrderErrorCode.ORDER_NOT_FOUND) }

        val orderIds = listOfNotNull(order.id)
        val items = orderItemRepository.findByOrderIdsWithProduct(orderIds).map { item ->
            OrderHistoryItemResponse(
                productName = item.product.name,
                quantity = item.quantity,
                price = item.price.toLong(),
            )
        }
        val payment = paymentRepository.findByOrderIds(orderIds).firstOrNull()?.let { p ->
            PaymentInfoResponse(
                amount = p.amount,
                pgProvider = p.pgProvider,
                paymentMethod = p.paymentMethod,
                status = p.status.name,
                paidAt = p.paidAt?.toString(),
            )
        }
        return OrderHistoryResponse(
            orderNumber = order.orderNumber,
            totalPrice = order.totalPrice.toLong(),
            status = order.status.name,
            orderedAt = order.orderedAt.toString(),
            items = items,
            payment = payment,
            canCancel = order.isCancellable(),
        )
    }
}
