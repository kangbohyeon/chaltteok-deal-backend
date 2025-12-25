package com.chaltteok.core.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(
    name = "tb_order_items",
    uniqueConstraints = [UniqueConstraint(name = "uk_order_item_uuid", columnNames = ["order_item_uuid"])]
)
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, referencedColumnName = "order_id")
    val order: Order? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false, referencedColumnName = "stock_id")
    val stock: DailyStock,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price_per_item", nullable = false)
    val pricePerItem: BigDecimal
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    var id: Long? = null

    @Column(name = "order_item_uuid", nullable = false, unique = true, length = 36)
    val orderItemUuid: String = UUID.randomUUID().toString()
}