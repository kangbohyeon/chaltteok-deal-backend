package com.chaltteok.owner.notification.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.notification.dto.NotificationListResponse
import com.chaltteok.owner.notification.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping
    fun getNotifications(): ResponseDTO<NotificationListResponse> =
        ResponseDTO.success(notificationService.getNotifications())

    @PatchMapping("/read")
    fun markAllRead(): ResponseDTO<Unit> {
        notificationService.markAllRead()
        return ResponseDTO.success(Unit)
    }
}
