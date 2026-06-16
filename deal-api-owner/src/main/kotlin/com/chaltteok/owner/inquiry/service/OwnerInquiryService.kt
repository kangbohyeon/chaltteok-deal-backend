package com.chaltteok.owner.inquiry.service

import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryPageResponse

interface OwnerInquiryService {
    fun getAll(page: Int, size: Int): OwnerInquiryPageResponse
    fun answer(inquiryUuid: String, request: AnswerRequest)
}
