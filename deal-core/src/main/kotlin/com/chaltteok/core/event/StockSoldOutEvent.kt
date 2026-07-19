package com.chaltteok.core.event

data class StockSoldOutEvent(val productName: String) : DomainEvent
