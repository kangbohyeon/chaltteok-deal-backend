package com.chaltteok.user.comment.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.comment.dto.CommentPageResponse
import com.chaltteok.user.comment.dto.CommentRequest
import com.chaltteok.user.comment.dto.CommentResponse
import com.chaltteok.user.comment.dto.ReplyRequest
import com.chaltteok.user.comment.service.UserCommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class CommentController(private val userCommentService: UserCommentService) {

    @GetMapping("/products/{productUuid}/comments")
    fun getComments(
        @PathVariable productUuid: String,
        authentication: Authentication?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseDTO<CommentPageResponse> {
        val userId = authentication?.principal as? Long
        return ResponseDTO.success(userCommentService.getComments(productUuid, userId, page, size))
    }

    @PostMapping("/products/{productUuid}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @PathVariable productUuid: String,
        authentication: Authentication,
        @Valid @RequestBody request: CommentRequest,
    ): ResponseDTO<CommentResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userCommentService.create(productUuid, userId, request))
    }

    @PostMapping("/comments/{commentUuid}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    fun replyComment(
        @PathVariable commentUuid: String,
        authentication: Authentication,
        @Valid @RequestBody request: ReplyRequest,
    ): ResponseDTO<CommentResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userCommentService.reply(commentUuid, userId, request))
    }

    @PutMapping("/comments/{commentUuid}")
    fun updateComment(
        @PathVariable commentUuid: String,
        authentication: Authentication,
        @Valid @RequestBody request: CommentRequest,
    ): ResponseDTO<CommentResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(userCommentService.update(commentUuid, userId, request))
    }

    @DeleteMapping("/comments/{commentUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable commentUuid: String,
        authentication: Authentication,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        userCommentService.delete(commentUuid, userId)
        return ResponseDTO.success(Unit)
    }
}
