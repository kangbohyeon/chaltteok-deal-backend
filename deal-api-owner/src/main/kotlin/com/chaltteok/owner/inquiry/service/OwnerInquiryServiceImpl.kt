package com.chaltteok.owner.inquiry.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.domain.enums.InquiryStatus
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryPageResponse
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse
import com.chaltteok.owner.inquiry.enums.InquiryErrorCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OwnerInquiryServiceImpl(
    private val inquiryRepository: InquiryRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository,
) : OwnerInquiryService {

    @Transactional(readOnly = true)
    override fun getAll(page: Int, size: Int): OwnerInquiryPageResponse {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val inquiryPage = inquiryRepository.findAllByOrderByCreatedAtDesc(pageable)

        val userIds = inquiryPage.content.map { it.userId }.distinct()
        val uuidMap = userRepository.findAllById(userIds).associate { it.id!! to it.userUuid }

        val inquiryUuids = inquiryPage.content.map { it.inquiryUuid }
        val attachmentMap = if (inquiryUuids.isNotEmpty()) {
            attachmentRepository.findAllByReferenceUuidInAndAttachmentType(inquiryUuids, AttachmentType.INQUIRY.name)
                .groupBy { it.referenceUuid!! }
        } else emptyMap()

        return OwnerInquiryPageResponse(
            content = inquiryPage.content.map {
                OwnerInquiryResponse.from(it, uuidMap[it.userId] ?: "", attachmentMap[it.inquiryUuid].orEmpty())
            },
            totalElements = inquiryPage.totalElements,
            totalPages = inquiryPage.totalPages,
            currentPage = page,
            pageSize = size,
        )
    }

    @Transactional(readOnly = true)
    override fun getInquiry(inquiryUuid: String): OwnerInquiryResponse {
        val inquiry = inquiryRepository.findByInquiryUuid(inquiryUuid)
            ?: throw BusinessException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        val userUuid = userRepository.findById(inquiry.userId).map { it.userUuid }.orElse("")
        val attachments = attachmentRepository.findAllByReferenceUuidInAndAttachmentType(
            listOf(inquiryUuid), AttachmentType.INQUIRY.name
        )
        return OwnerInquiryResponse.from(inquiry, userUuid, attachments)
    }

    @Transactional
    override fun answer(inquiryUuid: String, request: AnswerRequest) {
        val inquiry = inquiryRepository.findByInquiryUuid(inquiryUuid)
            ?: throw BusinessException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        inquiry.answer = request.answer
        inquiry.status = InquiryStatus.ANSWERED
        inquiry.answeredAt = LocalDateTime.now()
    }
}
