package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom {
}