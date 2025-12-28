package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long>,OrderItemRepositoryCustom {}