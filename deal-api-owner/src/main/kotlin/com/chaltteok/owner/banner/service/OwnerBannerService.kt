package com.chaltteok.owner.banner.service

import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse
import org.springframework.web.multipart.MultipartFile

interface OwnerBannerService {
    fun getAll(): List<BannerResponse>
    fun getBanner(bannerUuid: String): BannerResponse
    fun create(request: BannerRequest, image: MultipartFile?)
    fun update(bannerUuid: String, request: BannerRequest, image: MultipartFile?)
    fun delete(bannerUuid: String)
}
