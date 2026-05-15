package com.chaltteok.user.profile.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.profile.dto.ChangePasswordRequest
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse
import com.chaltteok.user.profile.service.UserProfileService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/me")
class UserProfileController(
    private val userProfileService: UserProfileService,
) {
    @GetMapping
    fun getProfile(
        @RequestHeader("X-User-Id") userId: Long,
    ): ResponseDTO<UserProfileResponse> =
        ResponseDTO.success(userProfileService.getProfile(userId))

    @PatchMapping
    fun updateNickname(
        @RequestHeader("X-User-Id") userId: Long,
        @Valid @RequestBody request: UpdateNicknameRequest,
    ): ResponseDTO<UserProfileResponse> =
        ResponseDTO.success(userProfileService.updateNickname(userId, request))

    @PatchMapping("/password")
    fun changePassword(
        @RequestHeader("X-User-Id") userId: Long,
        @Valid @RequestBody request: ChangePasswordRequest,
    ): ResponseDTO<Unit> {
        userProfileService.changePassword(userId, request)
        return ResponseDTO.success(Unit)
    }
}
