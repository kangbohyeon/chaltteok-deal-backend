package com.chaltteok.owner.inquiry.dto

import com.chaltteok.core.domain.Inquiry
import java.time.LocalDateTime

class OwnerInquiryResponse(
    val inquiryUuid: String,
    val userUuid: String,
    val title: String,
    val content: String,
    val status: String,
    val answer: String?,
    val answeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(inquiry: Inquiry, userUuid: String) = OwnerInquiryResponse(
            inquiryUuid = inquiry.inquiryUuid,
            userUuid = userUuid,
            title = inquiry.title,
            content = inquiry.content,
            status = inquiry.status,
            answer = inquiry.answer,
            answeredAt = inquiry.answeredAt,
            createdAt = inquiry.createdAt,
        )
    }
}
