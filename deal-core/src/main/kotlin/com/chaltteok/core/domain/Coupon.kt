package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.DiscountType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_coupons",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_coupon_uuid", columnNames = ["coupon_uuid"]),
        UniqueConstraint(name = "uk_coupon_code", columnNames = ["code"]),
    ]
)
class Coupon(
    @Column(name = "code", nullable = false, unique = true, length = 50)
    val code: String,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 10)
    val discountType: DiscountType,

    @Column(name = "discount_value", nullable = false)
    val discountValue: Int,

    @Column(name = "min_order_amount")
    val minOrderAmount: Int? = null,

    @Column(name = "max_discount_amount")
    val maxDiscountAmount: Int? = null,

    @Column(name = "total_quantity")
    val totalQuantity: Int? = null,

    @Column(name = "used_quantity", nullable = false)
    var usedQuantity: Int = 0,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    var id: Long? = null

    @Column(name = "coupon_uuid", nullable = false, unique = true, length = 36)
    val couponUuid: String = UUID.randomUUID().toString()

    @Version
    @Column(name = "version")
    var version: Long = 0

    fun isValid(orderAmount: Int): Boolean {
        val today = LocalDate.now()
        if (!isActive) return false
        if (today < startDate || today > endDate) return false
        if (totalQuantity != null && usedQuantity >= totalQuantity) return false
        if (minOrderAmount != null && orderAmount < minOrderAmount) return false
        return true
    }

    fun calculateDiscount(orderAmount: Int): Int {
        return when (discountType) {
            DiscountType.AMOUNT -> minOf(discountValue, orderAmount)
            DiscountType.RATE -> {
                val discount = orderAmount * discountValue / 100
                if (maxDiscountAmount != null) minOf(discount, maxDiscountAmount) else discount
            }
        }
    }

    fun use() {
        usedQuantity++
        updatedAt = LocalDateTime.now()
    }

    fun toggleActive() {
        isActive = !isActive
        updatedAt = LocalDateTime.now()
    }
}
