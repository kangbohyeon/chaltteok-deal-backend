package com.chaltteok.owner.banner.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

class BannerRequest(
    @field:Size(max = 200) val title: String? = null,
    @field:Size(max = 400) val subtitle: String? = null,
    @field:Pattern(regexp = "^(https?://.*)?$") val linkUrl: String? = null,
    @field:Pattern(regexp = "^(#[0-9A-Fa-f]{3,6})?$") val backgroundColor: String? = null,
    @field:Min(0) val sortOrder: Int = 0,
    val isVisible: Boolean = true,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
