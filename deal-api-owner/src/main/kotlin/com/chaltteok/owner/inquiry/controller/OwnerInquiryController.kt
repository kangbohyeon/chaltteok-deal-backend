package com.chaltteok.owner.inquiry.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.inquiry.dto.AnswerRequest
import com.chaltteok.owner.inquiry.dto.OwnerInquiryPageResponse
import com.chaltteok.owner.inquiry.dto.OwnerInquiryResponse
import com.chaltteok.owner.inquiry.service.OwnerInquiryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/inquiries")
class OwnerInquiryController(private val ownerInquiryService: OwnerInquiryService) {

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseDTO<OwnerInquiryPageResponse> =
        ResponseDTO.success(ownerInquiryService.getAll(page, size))

    @GetMapping("/{inquiryUuid}")
    fun getInquiry(@PathVariable inquiryUuid: String): ResponseDTO<OwnerInquiryResponse> =
        ResponseDTO.success(ownerInquiryService.getInquiry(inquiryUuid))

    @PutMapping("/{inquiryUuid}/answer")
    fun answer(
        @PathVariable inquiryUuid: String,
        @Valid @RequestBody request: AnswerRequest,
    ): ResponseDTO<Unit> {
        ownerInquiryService.answer(inquiryUuid, request)
        return ResponseDTO.success(Unit)
    }
}
