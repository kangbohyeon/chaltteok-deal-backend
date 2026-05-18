package com.chaltteok.user.inquiry.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
import com.chaltteok.user.inquiry.service.UserInquiryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/inquiries")
class UserInquiryController(private val userInquiryService: UserInquiryService) {

    @GetMapping
    fun getMyInquiries(
        @RequestHeader("X-User-Id") userId: Long,
    ): ResponseDTO<List<InquiryResponse>> =
        ResponseDTO.success(userInquiryService.getMyInquiries(userId))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestHeader("X-User-Id") userId: Long,
        @Valid @RequestBody request: InquiryRequest,
    ): ResponseDTO<InquiryResponse> =
        ResponseDTO.success(userInquiryService.create(userId, request))
}
