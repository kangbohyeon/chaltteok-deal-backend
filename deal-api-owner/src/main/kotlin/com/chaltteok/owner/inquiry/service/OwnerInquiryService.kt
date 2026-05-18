package com.chaltteok.owner.inquiry.service

import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse

interface OwnerInquiryService {
    fun getAll(): List<OwnerInquiryResponse>
    fun answer(inquiryUuid: String, request: AnswerRequest)
}
