package com.chaltteok.owner.inquiry.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse
import com.chaltteok.owner.inquiry.service.OwnerInquiryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/inquiries")
class OwnerInquiryController(private val ownerInquiryService: OwnerInquiryService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<OwnerInquiryResponse>> =
        ResponseDTO.success(ownerInquiryService.getAll())

    @PutMapping("/{inquiryUuid}/answer")
    fun answer(
        @PathVariable inquiryUuid: String,
        @Valid @RequestBody request: AnswerRequest,
    ): ResponseDTO<Unit> {
        ownerInquiryService.answer(inquiryUuid, request)
        return ResponseDTO.success(Unit)
    }
}
