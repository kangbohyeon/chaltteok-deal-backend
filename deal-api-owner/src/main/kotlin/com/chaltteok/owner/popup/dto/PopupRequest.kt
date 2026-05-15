package com.chaltteok.owner.popup.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

class PopupRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val content: String,
    val isVisible: Boolean = true,
    val location: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
