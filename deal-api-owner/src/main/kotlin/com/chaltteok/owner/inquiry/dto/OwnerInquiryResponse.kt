package com.chaltteok.owner.inquiry.dto

import com.chaltteok.core.domain.Inquiry
import java.time.LocalDateTime

class OwnerInquiryResponse(
    val inquiryUuid: String,
    val userId: Long,
    val title: String,
    val content: String,
    val status: String,
    val answer: String?,
    val answeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(inquiry: Inquiry) = OwnerInquiryResponse(
            inquiryUuid = inquiry.inquiryUuid,
            userId = inquiry.userId,
            title = inquiry.title,
            content = inquiry.content,
            status = inquiry.status,
            answer = inquiry.answer,
            answeredAt = inquiry.answeredAt,
            createdAt = inquiry.createdAt,
        )
    }
}
