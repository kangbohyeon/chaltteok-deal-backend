package com.chaltteok.core.repository.user

import java.time.LocalDateTime

interface UserRepositoryCustom {
    fun countNewUsers(from: LocalDateTime, to: LocalDateTime): Long
    fun countRepeatOrderUsers(from: LocalDateTime, to: LocalDateTime): Long
}