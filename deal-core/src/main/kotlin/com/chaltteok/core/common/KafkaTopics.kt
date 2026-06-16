package com.chaltteok.core.common

object KafkaTopics {
    const val DEAL_ORDER_EVENTS = "deal-order-events"
    const val ORDER_COMPLETED = "order-completed-events"
    const val ORDER_CANCELLED = "order-cancelled-events"
    const val PASSWORD_RESET_EMAILS = "password-reset-emails"
    const val DEAL_ORDER_EVENTS_DLT = "deal-order-events.DLT"
    const val ORDER_COMPLETED_DLT = "order-completed-events.DLT"
    const val ORDER_CANCELLED_DLT = "order-cancelled-events.DLT"
    const val PASSWORD_RESET_EMAILS_DLT = "password-reset-emails.DLT"

    val OUTBOX_TOPIC_MAP = mapOf(
        "ORDER_COMPLETED" to ORDER_COMPLETED,
        "ORDER_CANCELLED" to ORDER_CANCELLED,
    )
}
