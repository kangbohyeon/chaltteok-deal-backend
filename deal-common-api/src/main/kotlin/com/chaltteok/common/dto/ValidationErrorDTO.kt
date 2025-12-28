package com.chaltteok.common.dto

data class ValidationErrorDTO (
    val field: String,
    val value: String?,
    val reason : String?
)