package com.chaltteok.owner.notice.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.notice.dto.NoticeRequest
import com.chaltteok.owner.notice.dto.NoticeResponse
import com.chaltteok.owner.notice.service.OwnerNoticeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/notices")
class OwnerNoticeController(private val ownerNoticeService: OwnerNoticeService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<NoticeResponse>> =
        ResponseDTO.success(ownerNoticeService.getAll())

    @GetMapping("/{noticeUuid}")
    fun getNotice(@PathVariable noticeUuid: String): ResponseDTO<NoticeResponse> =
        ResponseDTO.success(ownerNoticeService.getNotice(noticeUuid))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: NoticeRequest): ResponseDTO<Unit> {
        ownerNoticeService.create(request)
        return ResponseDTO.success(Unit)
    }

    @PutMapping("/{noticeUuid}")
    fun update(
        @PathVariable noticeUuid: String,
        @Valid @RequestBody request: NoticeRequest,
    ): ResponseDTO<Unit> {
        ownerNoticeService.update(noticeUuid, request)
        return ResponseDTO.success(Unit)
    }

    @DeleteMapping("/{noticeUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable noticeUuid: String): ResponseDTO<Unit> {
        ownerNoticeService.delete(noticeUuid)
        return ResponseDTO.success(Unit)
    }
}
