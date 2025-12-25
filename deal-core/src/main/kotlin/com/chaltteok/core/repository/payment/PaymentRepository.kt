package com.chaltteok.core.repository.payment

import com.chaltteok.core.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}