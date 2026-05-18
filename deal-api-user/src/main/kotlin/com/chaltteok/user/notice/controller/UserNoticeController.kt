package com.chaltteok.user.notice.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.notice.dto.NoticeResponse
import com.chaltteok.user.notice.service.UserNoticeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/notices")
class UserNoticeController(private val userNoticeService: UserNoticeService) {

    @GetMapping
    fun getNotices(): ResponseDTO<List<NoticeResponse>> =
        ResponseDTO.success(userNoticeService.getVisibleNotices())
}
