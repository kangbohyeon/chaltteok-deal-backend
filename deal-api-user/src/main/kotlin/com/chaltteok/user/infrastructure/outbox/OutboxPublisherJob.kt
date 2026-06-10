package com.chaltteok.user.infrastructure.outbox

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.infrastructure.outbox.AbstractOutboxPublisherJob
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class OutboxPublisherJob(
    outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : AbstractOutboxPublisherJob(outboxEventRepository) {

    override val source = OutboxEvent.SOURCE_API_USER

    override fun sendToKafka(topic: String, key: String, payload: String): CompletableFuture<*> =
        kafkaTemplate.send(topic, key, payload)
}
