package com.chaltteok.user.inquiry.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.inquiry.dto.InquiryRequest
import com.chaltteok.user.inquiry.dto.InquiryResponse
import com.chaltteok.user.inquiry.service.UserInquiryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/inquiries")
class UserInquiryController(private val userInquiryService: UserInquiryService) {

    @GetMapping
    fun getMyInquiries(authentication: Authentication): ResponseDTO<List<InquiryResponse>> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userInquiryService.getMyInquiries(userId))
    }

    @GetMapping("/{inquiryUuid}")
    fun getMyInquiry(
        authentication: Authentication,
        @PathVariable inquiryUuid: String,
    ): ResponseDTO<InquiryResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userInquiryService.getMyInquiry(userId, inquiryUuid))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        authentication: Authentication,
        @Valid @RequestBody request: InquiryRequest,
    ): ResponseDTO<InquiryResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userInquiryService.create(userId, request))
    }

    @PutMapping("/{inquiryUuid}")
    fun update(
        authentication: Authentication,
        @PathVariable inquiryUuid: String,
        @Valid @RequestBody request: InquiryRequest,
    ): ResponseDTO<InquiryResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userInquiryService.update(userId, inquiryUuid, request))
    }

    @DeleteMapping("/{inquiryUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        authentication: Authentication,
        @PathVariable inquiryUuid: String,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        userInquiryService.delete(userId, inquiryUuid)
        return ResponseDTO.success(Unit)
    }
}
