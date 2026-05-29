package com.chaltteok.owner.banner.service

import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse

interface OwnerBannerService {
    fun getAll(): List<BannerResponse>
    fun create(request: BannerRequest)
    fun update(bannerUuid: String, request: BannerRequest)
    fun delete(bannerUuid: String)
}
