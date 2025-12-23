package repository.payment

import domain.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}