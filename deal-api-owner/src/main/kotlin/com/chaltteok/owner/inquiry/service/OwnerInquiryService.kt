package com.chaltteok.owner.inquiry.service

import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryPageResponse
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse

interface OwnerInquiryService {
    fun getAll(page: Int, size: Int): OwnerInquiryPageResponse
    fun getInquiry(inquiryUuid: String): OwnerInquiryResponse
    fun answer(inquiryUuid: String, request: AnswerRequest)
}
