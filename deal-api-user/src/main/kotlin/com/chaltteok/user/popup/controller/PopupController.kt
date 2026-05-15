package com.chaltteok.user.popup.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.popup.dto.PopupResponse
import com.chaltteok.user.popup.service.UserPopupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/popups")
class PopupController(private val userPopupService: UserPopupService) {

    @GetMapping
    fun getPopups(): ResponseDTO<List<PopupResponse>> =
        ResponseDTO.success(userPopupService.getActivePopups())
}
