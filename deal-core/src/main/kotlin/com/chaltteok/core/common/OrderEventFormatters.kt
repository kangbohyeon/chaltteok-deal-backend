package com.chaltteok.core.common

import java.time.format.DateTimeFormatter

object OrderEventFormatters {
    val DISPLAY: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
