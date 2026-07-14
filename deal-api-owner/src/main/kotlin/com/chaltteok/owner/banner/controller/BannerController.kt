package com.chaltteok.owner.banner.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse
import com.chaltteok.owner.banner.service.OwnerBannerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.RequestPart

@RestController
@RequestMapping("/api/v1/owner/banners")
class BannerController(private val ownerBannerService: OwnerBannerService) {

    @GetMapping
    fun getAll(): ResponseDTO<List<BannerResponse>> =
        ResponseDTO.success(ownerBannerService.getAll())

    @GetMapping("/{bannerUuid}")
    fun getBanner(@PathVariable bannerUuid: String): ResponseDTO<BannerResponse> =
        ResponseDTO.success(ownerBannerService.getBanner(bannerUuid))

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @RequestPart("image", required = false) image: MultipartFile?,
        @Valid @RequestPart("data") request: BannerRequest,
    ): ResponseEntity<ResponseDTO<Any>> {
        ownerBannerService.create(request, image)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/{bannerUuid}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun update(
        @PathVariable bannerUuid: String,
        @RequestPart("image", required = false) image: MultipartFile?,
        @Valid @RequestPart("data") request: BannerRequest,
    ): ResponseDTO<Any> {
        ownerBannerService.update(bannerUuid, request, image)
        return ResponseDTO.success()
    }

    @DeleteMapping("/{bannerUuid}")
    fun delete(@PathVariable bannerUuid: String): ResponseDTO<Any> {
        ownerBannerService.delete(bannerUuid)
        return ResponseDTO.success()
    }
}
