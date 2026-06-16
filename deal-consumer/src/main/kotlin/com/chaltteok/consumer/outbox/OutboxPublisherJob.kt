package com.chaltteok.consumer.outbox

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.infrastructure.outbox.AbstractOutboxPublisherJob
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OutboxPublisherJob(
    outboxEventRepository: OutboxEventRepository,
    kafkaTemplate: KafkaTemplate<String, String>,
) : AbstractOutboxPublisherJob(outboxEventRepository, kafkaTemplate) {
    override val source = OutboxEvent.SOURCE_CONSUMER
}
