package com.chaltteok.user.profile.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.profile.dto.ChangePasswordRequest
import com.chaltteok.user.profile.dto.ConsentUpdateRequest
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse
import com.chaltteok.user.profile.service.UserProfileService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/me")
class UserProfileController(
    private val userProfileService: UserProfileService,
) {
    @GetMapping
    fun getProfile(authentication: Authentication): ResponseDTO<UserProfileResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userProfileService.getProfile(userId))
    }

    @PatchMapping
    fun updateNickname(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateNicknameRequest,
    ): ResponseDTO<UserProfileResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userProfileService.updateNickname(userId, request))
    }

    @PatchMapping("/password")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangePasswordRequest,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        userProfileService.changePassword(userId, request)
        return ResponseDTO.success(Unit)
    }

    @PatchMapping("/consents")
    fun updateConsent(
        authentication: Authentication,
        @Valid @RequestBody request: ConsentUpdateRequest,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        userProfileService.updateConsent(userId, request)
        return ResponseDTO.success(Unit)
    }
}
