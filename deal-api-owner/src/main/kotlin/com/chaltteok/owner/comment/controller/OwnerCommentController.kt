package com.chaltteok.owner.comment.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.comment.dto.OwnerCommentPageResponse
import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest
import com.chaltteok.owner.comment.service.OwnerCommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/comments")
class OwnerCommentController(private val ownerCommentService: OwnerCommentService) {

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseDTO<OwnerCommentPageResponse> =
        ResponseDTO.success(ownerCommentService.getAll(page, size))

    @DeleteMapping("/{commentUuid}")
    fun delete(
        authentication: Authentication,
        @PathVariable commentUuid: String,
    ): ResponseEntity<Void> {
        ownerCommentService.delete(commentUuid, authentication.principal as Long)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{commentUuid}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    fun reply(
        authentication: Authentication,
        @PathVariable commentUuid: String,
        @Valid @RequestBody request: OwnerReplyRequest,
    ): ResponseDTO<OwnerCommentResponse> =
        ResponseDTO.success(ownerCommentService.reply(commentUuid, request, authentication.principal as Long))

    @PutMapping("/{commentUuid}/reply")
    fun updateReply(
        authentication: Authentication,
        @PathVariable commentUuid: String,
        @Valid @RequestBody request: OwnerReplyRequest,
    ): ResponseDTO<OwnerCommentResponse> =
        ResponseDTO.success(ownerCommentService.updateReply(commentUuid, request, authentication.principal as Long))

    @DeleteMapping("/{commentUuid}/reply")
    fun deleteReply(
        authentication: Authentication,
        @PathVariable commentUuid: String,
    ): ResponseEntity<Void> {
        ownerCommentService.deleteReply(commentUuid, authentication.principal as Long)
        return ResponseEntity.noContent().build()
    }
}
