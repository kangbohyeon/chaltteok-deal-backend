package com.chaltteok.user.auth.email

interface UserEmailService {
    fun sendPasswordReset(email: String, tempPassword: String)
}
