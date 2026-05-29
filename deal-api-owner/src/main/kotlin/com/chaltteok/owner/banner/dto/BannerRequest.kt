package com.chaltteok.owner.banner.dto

import java.time.LocalDate

class BannerRequest(
    val title: String? = null,
    val subtitle: String? = null,
    val imageUrl: String? = null,
    val linkUrl: String? = null,
    val backgroundColor: String? = null,
    val sortOrder: Int = 0,
    val isVisible: Boolean = true,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
