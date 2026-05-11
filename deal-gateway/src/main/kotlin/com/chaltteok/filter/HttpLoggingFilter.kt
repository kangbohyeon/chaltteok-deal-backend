package com.chaltteok.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Component
class HttpLoggingFilter(
    private val objectMapper: ObjectMapper,
) : GlobalFilter, Ordered {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val startTime = System.currentTimeMillis()
        val remoteIp = getClientIp(exchange)
        val method = request.method.name()
        val uri = request.uri.path

        val headersJson = try {
            objectMapper.writeValueAsString(request.headers.toSingleValueMap())
        } catch (e: Exception) {
            log.warn("Header json converting failed", e)
            "{}"
        }

        val paramsJson = try {
            objectMapper.writeValueAsString(request.queryParams.toSingleValueMap())
        } catch (e: Exception) {
            log.warn("Param json converting failed", e)
            "{}"
        }

        log.info(
            "Request: Remote IP: {}, Headers: {}, Method: {}, URI: {}, Parameter: {}",
            remoteIp, headersJson, method, uri, paramsJson,
        )

        val decoratedResponse = object : ServerHttpResponseDecorator(exchange.response) {
            override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
                return DataBufferUtils.join(Flux.from(body))
                    .flatMap { combined ->
                        val bytes = ByteArray(combined.readableByteCount())
                        combined.read(bytes)
                        DataBufferUtils.release(combined)

                        val duration = System.currentTimeMillis() - startTime
                        val status = delegate.statusCode?.value() ?: 0

                        log.info(
                            "Response: Status: {}, Method: {}, URI: {}, Time: {}ms, responseBody: {}",
                            status, method, uri, duration, truncate(bytes),
                        )

                        super.writeWith(Mono.just(exchange.response.bufferFactory().wrap(bytes)))
                    }
            }
        }

        return chain.filter(exchange.mutate().response(decoratedResponse).build())
    }

    private fun truncate(bytes: ByteArray): String {
        val length = minOf(bytes.size, 1000)
        return try {
            String(bytes, 0, length, StandardCharsets.UTF_8).replace(Regex("[\r\n]+"), " ")
        } catch (e: Exception) {
            ""
        }
    }

    private fun getClientIp(exchange: ServerWebExchange): String {
        val proxyHeaders = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP",
            "X-RealIP",
        )

        val ip = proxyHeaders.firstNotNullOfOrNull { header ->
            exchange.request.headers.getFirst(header)
                ?.takeIf { it.isNotBlank() && !it.equals("unknown", ignoreCase = true) }
        } ?: exchange.request.remoteAddress?.address?.hostAddress ?: ""

        return if (ip.contains(",")) ip.split(",")[0].trim() else ip
    }
}
