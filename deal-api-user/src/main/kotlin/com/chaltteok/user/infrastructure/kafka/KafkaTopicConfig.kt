package com.chaltteok.user.infrastructure.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun dealOrderEventsTopic(): NewTopic =
        TopicBuilder.name("deal-order-events")
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun orderCompletedEventsTopic(): NewTopic =
        TopicBuilder.name("order-completed-events")
            .partitions(3)
            .replicas(1)
            .build()
}
