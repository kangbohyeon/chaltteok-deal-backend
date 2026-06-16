package com.chaltteok.consumer.order.service

interface OrderProcessService {
    fun processOrder(command: OrderProcessCommand)
}
