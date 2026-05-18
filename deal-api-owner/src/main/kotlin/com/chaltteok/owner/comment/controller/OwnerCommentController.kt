package com.chaltteok.owner.comment.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest
import com.chaltteok.owner.comment.service.OwnerCommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/comments")
class OwnerCommentController(private val ownerCommentService: OwnerCommentService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<OwnerCommentResponse>> =
        ResponseDTO.success(ownerCommentService.getAll())

    @DeleteMapping("/{commentUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable commentUuid: String): ResponseDTO<Unit> {
        ownerCommentService.delete(commentUuid)
        return ResponseDTO.success(Unit)
    }

    @PostMapping("/{commentUuid}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    fun reply(
        @PathVariable commentUuid: String,
        @Valid @RequestBody request: OwnerReplyRequest,
    ): ResponseDTO<OwnerCommentResponse> =
        ResponseDTO.success(ownerCommentService.reply(commentUuid, request))
}
