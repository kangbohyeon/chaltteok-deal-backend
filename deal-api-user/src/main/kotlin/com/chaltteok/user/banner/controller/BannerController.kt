package com.chaltteok.user.banner.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.banner.dto.BannerResponse
import com.chaltteok.user.banner.service.UserBannerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/banners")
class BannerController(private val userBannerService: UserBannerService) {

    @GetMapping
    fun getBanners(): ResponseDTO<List<BannerResponse>> =
        ResponseDTO.success(userBannerService.getActiveBanners())
}
