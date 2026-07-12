package com.chaltteok.user.inquiry.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Inquiry
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.user.file.enums.FileErrorCode
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

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
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다: $inquiryUuid")
        if (inquiry.userId != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 문의만 삭제할 수 있습니다.")
        }
        if (inquiry.status != "PENDING") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "답변 완료된 문의는 삭제할 수 없습니다.")
        }
        attachmentRepository.deleteByReferenceUuidAndAttachmentType(inquiryUuid, AttachmentType.INQUIRY.name)
        inquiryRepository.delete(inquiry)
    }
}
