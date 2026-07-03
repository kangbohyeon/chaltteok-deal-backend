package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.domain.enums.PaymentStatus
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.order.dto.OrderHistoryItemResponse
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.dto.PaymentInfoResponse
import com.chaltteok.user.order.enums.OrderErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

private val logger = KotlinLogging.logger {}

@Service
class OrderServiceImpl(
    private val timeSaleStockRepository: TimeSaleStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val outboxEventWriter: OutboxEventWriter,
) : OrderService {

    @Transactional
    override fun placeOrder(userId: Long, request: OrderRequest): OrderResponse {
        val timeSaleStock = timeSaleStockRepository.findByStockUuidWithLock(request.stockUuid)
            ?: throw BusinessException(OrderErrorCode.TIME_SALE_STOCK_NOT_FOUND)

        if (timeSaleStock.status != TimeSaleStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }
        if (timeSaleStock.remainStock < request.quantity) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val maxPurchaseCount = timeSaleStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val participated = eventHistoryRepository.countByUser_IdAndTimeSaleStock_Id(userId, timeSaleStock.id)
            if (participated + request.quantity > maxPurchaseCount) {
                if (participated == 0L) throw BusinessException(OrderErrorCode.EXCEEDS_MAX_PURCHASE_COUNT)
                throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
            }
        }

        timeSaleStock.decrease(request.quantity)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }
        val totalPrice = timeSaleStock.salePrice.toLong() * request.quantity
        val order = orderRepository.save(
            Order(user = user, totalPrice = totalPrice.toInt(), status = OrderStatus.COMPLETED)
        )
        orderItemRepository.save(
            OrderItem(order = order, product = timeSaleStock.product, quantity = request.quantity, price = timeSaleStock.salePrice)
        )
        paymentRepository.save(
            Payment(order = order, amount = totalPrice.toInt(), status = PaymentStatus.SUCCESS, paymentMethod = request.paymentMethod.name, paidAt = LocalDateTime.now())
        )

        outboxEventWriter.write(
            source = OutboxEvent.SOURCE_API_USER,
            aggregateId = order.orderNumber,
            eventType = OutboxEvent.TYPE_ORDER_COMPLETED,
            event = OrderCompletedEvent(
                orderId = order.id ?: error("Order ID null"),
                orderNumber = order.orderNumber,
                userName = user.nickname,
                productName = timeSaleStock.product.name,
                totalAmount = totalPrice,
                orderedAt = order.orderedAt,
            )
        )

        logger.info { "타임세일 동기 주문 완료 — orderNumber=${order.orderNumber}, userId=$userId, stockUuid=${request.stockUuid}" }
        return OrderResponse.completed(order.orderNumber, totalPrice)
    }

    @Transactional
    override fun cancelOrder(userId: Long, orderNumber: String) {
        val order = orderRepository.findByOrderNumberAndUser_Id(orderNumber, userId)
            .orElseThrow { BusinessException(OrderErrorCode.ORDER_NOT_FOUND) }

        if (order.status == OrderStatus.CANCELLED) {
            throw BusinessException(OrderErrorCode.ORDER_ALREADY_CANCELLED)
        }
        if (!order.isCancellable()) {
            throw BusinessException(OrderErrorCode.ORDER_NOT_CANCELLABLE)
        }

        order.cancel()

        val orderId = order.id ?: error("Order ID null")
        paymentRepository.findByOrderId(orderId)?.cancel()

        outboxEventWriter.write(
            source = OutboxEvent.SOURCE_API_USER,
            aggregateId = order.orderNumber,
            eventType = OutboxEvent.TYPE_ORDER_CANCELLED,
            event = OrderCancelledEvent(
                orderId = orderId,
                orderNumber = order.orderNumber,
                userName = order.user.nickname,
                totalAmount = order.totalPrice.toLong(),
                cancelledAt = LocalDateTime.now(),
            )
        )
    }

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
                throw BusinessException(OrderErrorCode.INVALID_DATE_FORMAT)
            }
        }
        val to = toDate?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) {
                throw BusinessException(OrderErrorCode.INVALID_DATE_FORMAT)
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
        val itemsByOrderId = orderItemRepository.findByOrderIdsWithProduct(orderIds).groupBy { it.order?.id }
        val paymentByOrderId = paymentRepository.findByOrderIds(orderIds).associateBy { it.order.id }

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

        val orderId = order.id ?: error("Order ID null")
        val items = orderItemRepository.findByOrderIdWithProduct(orderId).map { item ->
            OrderHistoryItemResponse(
                productName = item.product.name,
                quantity = item.quantity,
                price = item.price.toLong(),
            )
        }
        val payment = paymentRepository.findByOrderId(orderId)?.let { p ->
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
