package com.chaltteok.core.infrastructure.outbox

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.event.DomainEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class OutboxEventWriter(
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper,
) {
    fun write(source: String, aggregateId: String, eventType: String, event: DomainEvent) {
        outboxEventRepository.save(
            OutboxEvent(
                source = source,
                aggregateId = aggregateId,
                eventType = eventType,
                payload = objectMapper.writeValueAsString(event),
            )
        )
    }
}
