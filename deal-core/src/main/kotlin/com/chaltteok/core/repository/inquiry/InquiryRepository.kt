package com.chaltteok.core.repository.inquiry

import com.chaltteok.core.domain.Inquiry
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long> {
    fun findByInquiryUuid(uuid: String): Inquiry?
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Inquiry>
    fun findAllByOrderByCreatedAtDesc(): List<Inquiry>
}
