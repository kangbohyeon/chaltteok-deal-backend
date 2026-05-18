package com.chaltteok.owner.inquiry.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse
import com.chaltteok.owner.inquiry.enums.InquiryErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OwnerInquiryServiceImpl(
    private val inquiryRepository: InquiryRepository,
) : OwnerInquiryService {

    @Transactional(readOnly = true)
    override fun getAll(): List<OwnerInquiryResponse> =
        inquiryRepository.findAllByOrderByCreatedAtDesc().map { OwnerInquiryResponse.from(it) }

    @Transactional
    override fun answer(inquiryUuid: String, request: AnswerRequest) {
        val inquiry = inquiryRepository.findByInquiryUuid(inquiryUuid)
            ?: throw BusinessException(InquiryErrorCode.INQUIRY_NOT_FOUND)
        inquiry.answer = request.answer
        inquiry.status = "ANSWERED"
        inquiry.answeredAt = LocalDateTime.now()
    }
}
