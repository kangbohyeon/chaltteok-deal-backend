package com.chaltteok.owner.profile.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest
import com.chaltteok.owner.profile.dto.OwnerProfileResponse
import com.chaltteok.owner.profile.dto.WithdrawOwnerRequest
import com.chaltteok.owner.profile.service.OwnerProfileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/me")
class OwnerProfileController(private val ownerProfileService: OwnerProfileService) {

    @GetMapping
    fun getProfile(authentication: Authentication): ResponseDTO<OwnerProfileResponse> {
        val ownerId = authentication.principal as Long
        return ResponseDTO.success(ownerProfileService.getProfile(ownerId))
    }

    @PatchMapping("/password")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangeOwnerPasswordRequest,
    ): ResponseDTO<Unit> {
        val ownerId = authentication.principal as Long
        ownerProfileService.changePassword(ownerId, request)
        return ResponseDTO.success(Unit)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdraw(
        authentication: Authentication,
        @Valid @RequestBody request: WithdrawOwnerRequest,
    ): ResponseDTO<Unit> {
        val ownerId = authentication.principal as Long
        ownerProfileService.withdraw(ownerId, request.currentPassword)
        return ResponseDTO.success(Unit)
    }
}
