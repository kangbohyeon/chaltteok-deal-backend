package com.chaltteok.user.inquiry.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Inquiry
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.user.file.enums.FileErrorCode
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
import com.chaltteok.user.inquiry.enums.InquiryErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInquiryServiceImpl(
    private val inquiryRepository: InquiryRepository,
    private val attachmentRepository: AttachmentRepository,
) : UserInquiryService {

    @Transactional(readOnly = true)
    override fun getMyInquiries(userId: Long): List<InquiryResponse> {
        val inquiries = inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId)
        val inquiryUuids = inquiries.map { it.inquiryUuid }
        val attachmentMap = if (inquiryUuids.isNotEmpty()) {
            attachmentRepository.findAllByReferenceUuidInAndAttachmentType(inquiryUuids, AttachmentType.INQUIRY.name)
                .groupBy { it.referenceUuid!! }
        } else emptyMap()
        return inquiries.map { InquiryResponse.from(it, attachmentMap[it.inquiryUuid].orEmpty()) }
    }

    @Transactional(readOnly = true)
    override fun getMyInquiry(userId: Long, inquiryUuid: String): InquiryResponse {
        val inquiry = inquiryRepository.findByInquiryUuid(inquiryUuid)
            ?.takeIf { it.userId == userId }
            ?: throw BusinessException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        val attachments = attachmentRepository.findAllByReferenceUuidInAndAttachmentType(
            listOf(inquiryUuid), AttachmentType.INQUIRY.name
        )
        return InquiryResponse.from(inquiry, attachments)
    }

    @Transactional
    override fun create(userId: Long, request: InquiryRequest): InquiryResponse {
        val inquiry = inquiryRepository.save(
            Inquiry(
                userId = userId,
                title = request.title,
                content = request.content,
            )
        )
        if (request.attachmentUuids.isNotEmpty()) {
            val updated = attachmentRepository.updateReferenceByUuids(
                request.attachmentUuids,
                inquiry.inquiryUuid,
                AttachmentType.INQUIRY.name
            )
            if (updated != request.attachmentUuids.size) {
                throw BusinessException(FileErrorCode.ATTACHMENT_OWNERSHIP_VIOLATION)
            }
        }
        return InquiryResponse.from(inquiry)
    }

    @Transactional
    override fun delete(userId: Long, inquiryUuid: String) {
        val inquiry = inquiryRepository.findByInquiryUuid(inquiryUuid)
            ?: throw BusinessException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        if (inquiry.userId != userId) {
            throw BusinessException(InquiryErrorCode.INQUIRY_ACCESS_DENIED)
        }
        if (inquiry.status != "PENDING") {
            throw BusinessException(InquiryErrorCode.INQUIRY_ALREADY_ANSWERED)
        }
        attachmentRepository.deleteByReferenceUuidAndAttachmentType(inquiryUuid, AttachmentType.INQUIRY.name)
        inquiryRepository.delete(inquiry)
    }
}
