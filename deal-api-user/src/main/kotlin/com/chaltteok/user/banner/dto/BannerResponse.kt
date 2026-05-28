package com.chaltteok.user.banner.dto

import com.chaltteok.core.domain.Banner
import java.time.LocalDate

class BannerResponse(
    val bannerUuid: String,
    val title: String?,
    val subtitle: String?,
    val imageUrl: String?,
    val linkUrl: String?,
    val backgroundColor: String?,
    val sortOrder: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
) {
    companion object {
        fun from(banner: Banner) = BannerResponse(
            bannerUuid = banner.bannerUuid,
            title = banner.title,
            subtitle = banner.subtitle,
            imageUrl = banner.imageUrl,
            linkUrl = banner.linkUrl,
            backgroundColor = banner.backgroundColor,
            sortOrder = banner.sortOrder,
            startDate = banner.startDate,
            endDate = banner.endDate,
        )
    }
}
