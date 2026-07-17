package com.chaltteok.user.inquiry.dto

import com.chaltteok.core.domain.Attachment
import com.chaltteok.core.domain.Inquiry
import com.chaltteok.user.comment.dto.AttachmentInfo
import java.time.LocalDateTime

class InquiryResponse(
    val inquiryUuid: String,
    val title: String,
    val content: String,
    val status: String,
    val answer: String?,
    val answeredAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val attachments: List<AttachmentInfo> = emptyList(),
) {
    companion object {
        fun from(inquiry: Inquiry, attachments: List<Attachment> = emptyList()) = InquiryResponse(
            inquiryUuid = inquiry.inquiryUuid,
            title = inquiry.title,
            content = inquiry.content,
            status = inquiry.status.name,
            answer = inquiry.answer,
            answeredAt = inquiry.answeredAt,
            createdAt = inquiry.createdAt,
            attachments = attachments.map { AttachmentInfo(it.attachmentUuid, it.fileUrl, it.originalFilename) },
        )
    }
}
