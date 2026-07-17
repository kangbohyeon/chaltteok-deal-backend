package com.chaltteok.owner.inquiry.dto

import com.chaltteok.core.domain.Attachment
import com.chaltteok.core.domain.Inquiry
import com.chaltteok.owner.comment.dto.AttachmentInfo
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
    val attachments: List<AttachmentInfo> = emptyList(),
) {
    companion object {
        fun from(inquiry: Inquiry, userUuid: String, attachments: List<Attachment> = emptyList()) = OwnerInquiryResponse(
            inquiryUuid = inquiry.inquiryUuid,
            userUuid = userUuid,
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
