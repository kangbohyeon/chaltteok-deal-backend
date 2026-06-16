package com.chaltteok.owner.banner.dto

import com.chaltteok.core.domain.Banner
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

class BannerResponse(
    val bannerUuid: String,
    val title: String?,
    val subtitle: String?,
    val imageUrl: String?,
    val linkUrl: String?,
    val backgroundColor: String?,
    val sortOrder: Int,
    @get:JsonProperty("isVisible") val isVisible: Boolean,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val createdAt: LocalDateTime,
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
            isVisible = banner.isVisible,
            startDate = banner.startDate,
            endDate = banner.endDate,
            createdAt = banner.createdAt,
        )
    }
}
