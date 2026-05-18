package com.chaltteok.user.inquiry.service

import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse

interface UserInquiryService {
    fun getMyInquiries(userId: Long): List<InquiryResponse>
    fun create(userId: Long, request: InquiryRequest): InquiryResponse
}
