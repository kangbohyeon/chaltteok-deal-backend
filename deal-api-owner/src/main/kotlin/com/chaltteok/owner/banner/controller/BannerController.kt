package com.chaltteok.owner.banner.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse
import com.chaltteok.owner.banner.service.OwnerBannerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/banners")
class BannerController(private val ownerBannerService: OwnerBannerService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<BannerResponse>> =
        ResponseDTO.success(ownerBannerService.getAll())

    @PostMapping
    fun create(@RequestBody request: BannerRequest): ResponseEntity<ResponseDTO<Any>> {
        ownerBannerService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/{bannerUuid}")
    fun update(
        @PathVariable bannerUuid: String,
        @RequestBody request: BannerRequest,
    ): ResponseDTO<Any> {
        ownerBannerService.update(bannerUuid, request)
        return ResponseDTO.success()
    }

    @DeleteMapping("/{bannerUuid}")
    fun delete(@PathVariable bannerUuid: String): ResponseDTO<Any> {
        ownerBannerService.delete(bannerUuid)
        return ResponseDTO.success()
    }
}
