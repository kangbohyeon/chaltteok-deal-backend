package com.chaltteok.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway")
class GatewayProperties(
    val internalSecret: String = "",
    val publicPaths: List<String> = emptyList(),
    val ssePaths: List<String> = emptyList(),
    val ownerPaths: List<String> = emptyList(),
) {
    init {
        require(internalSecret.toByteArray(Charsets.UTF_8).size >= 32) {
            "gateway.internal-secret must be at least 32 bytes (current: ${internalSecret.toByteArray(Charsets.UTF_8).size})"
        }
        require(ownerPaths.isNotEmpty()) {
            "gateway.owner-paths must not be empty — ROLE_OWNER authorization would be silently bypassed"
        }
    }
}
