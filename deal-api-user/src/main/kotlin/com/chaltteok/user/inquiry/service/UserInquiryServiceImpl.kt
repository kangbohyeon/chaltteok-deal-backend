package com.chaltteok.user.inquiry.service

import com.chaltteok.core.domain.Inquiry
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
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
            attachmentRepository.updateReferenceByUuids(
                request.attachmentUuids,
                inquiry.inquiryUuid,
                AttachmentType.INQUIRY.name
            )
        }
        return InquiryResponse.from(inquiry)
    }
}
