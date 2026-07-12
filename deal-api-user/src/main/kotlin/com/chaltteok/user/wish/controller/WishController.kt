package com.chaltteok.user.wish.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.wish.dto.WishListResponse
import com.chaltteok.user.wish.service.WishService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class WishController(private val wishService: WishService) {

    @GetMapping("/wishes")
    fun getWishes(authentication: Authentication): ResponseDTO<WishListResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(wishService.getWishes(userId))
    }

    @PostMapping("/products/{productUuid}/wish")
    @ResponseStatus(HttpStatus.CREATED)
    fun addWish(
        authentication: Authentication,
        @PathVariable productUuid: String,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        wishService.addWish(userId, productUuid)
        return ResponseDTO.success(Unit)
    }

    @DeleteMapping("/products/{productUuid}/wish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeWish(
        authentication: Authentication,
        @PathVariable productUuid: String,
    ): ResponseDTO<Unit> {
        val userId = authentication.principal as Long
        wishService.removeWish(userId, productUuid)
        return ResponseDTO.success(Unit)
    }
}
