package com.chaltteok.user.infrastructure.kafka

import com.chaltteok.core.common.KafkaTopics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean fun dealOrderEventsTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.DEAL_ORDER_EVENTS).partitions(3).replicas(1).build()

    @Bean fun orderCompletedEventsTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.ORDER_COMPLETED).partitions(3).replicas(1).build()

    @Bean fun orderCancelledEventsTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.ORDER_CANCELLED).partitions(3).replicas(1).build()

    @Bean fun dealOrderEventsDltTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.DEAL_ORDER_EVENTS_DLT).partitions(3).replicas(1).build()

    @Bean fun orderCompletedEventsDltTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.ORDER_COMPLETED_DLT).partitions(3).replicas(1).build()

    @Bean fun orderCancelledEventsDltTopic(): NewTopic =
        TopicBuilder.name(KafkaTopics.ORDER_CANCELLED_DLT).partitions(3).replicas(1).build()
}
