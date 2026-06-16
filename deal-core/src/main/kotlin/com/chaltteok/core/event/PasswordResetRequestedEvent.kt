package com.chaltteok.core.event

data class PasswordResetRequestedEvent(
    val email: String,
    val tempPassword: String,
) : DomainEvent
