package com.chaltteok.core.repository.coupon

import com.chaltteok.core.domain.Coupon
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CouponRepository : JpaRepository<Coupon, Long> {
    fun findByCouponUuid(uuid: String): Optional<Coupon>
    fun findByCode(code: String): Optional<Coupon>
    fun findAllByOrderByCreatedAtDesc(): List<Coupon>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.code = :code")
    fun findByCodeForUpdate(@Param("code") code: String): Optional<Coupon>
}
