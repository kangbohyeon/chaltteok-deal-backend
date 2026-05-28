package com.chaltteok.user.banner.service

import com.chaltteok.user.banner.dto.BannerResponse

interface UserBannerService {
    fun getActiveBanners(): List<BannerResponse>
}
