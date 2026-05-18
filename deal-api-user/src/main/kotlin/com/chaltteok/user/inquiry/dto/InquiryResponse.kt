package com.chaltteok.user.inquiry.dto

import com.chaltteok.core.domain.Inquiry
import java.time.LocalDateTime

class InquiryResponse(
    val inquiryUuid: String,
    val title: String,
    val content: String,
    val status: String,
    val answer: String?,
    val answeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(inquiry: Inquiry) = InquiryResponse(
            inquiryUuid = inquiry.inquiryUuid,
            title = inquiry.title,
            content = inquiry.content,
            status = inquiry.status,
            answer = inquiry.answer,
            answeredAt = inquiry.answeredAt,
            createdAt = inquiry.createdAt,
        )
    }
}
