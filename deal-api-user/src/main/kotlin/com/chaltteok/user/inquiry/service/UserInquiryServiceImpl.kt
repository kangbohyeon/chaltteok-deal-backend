package com.chaltteok.user.inquiry.service

import com.chaltteok.core.domain.Inquiry
import com.chaltteok.core.repository.inquiry.InquiryRepository
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInquiryServiceImpl(
    private val inquiryRepository: InquiryRepository,
) : UserInquiryService {

    @Transactional(readOnly = true)
    override fun getMyInquiries(userId: Long): List<InquiryResponse> =
        inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId).map { InquiryResponse.from(it) }

    @Transactional
    override fun create(userId: Long, request: InquiryRequest): InquiryResponse {
        val inquiry = inquiryRepository.save(
            Inquiry(
                userId = userId,
                title = request.title,
                content = request.content,
            )
        )
        return InquiryResponse.from(inquiry)
    }
}
