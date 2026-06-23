package com.chaltteok.core.repository.coupon

import com.chaltteok.core.domain.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CouponRepository : JpaRepository<Coupon, Long> {
    fun findByCouponUuid(uuid: String): Optional<Coupon>
    fun findByCode(code: String): Optional<Coupon>
    fun findAllByOrderByCreatedAtDesc(): List<Coupon>
}
