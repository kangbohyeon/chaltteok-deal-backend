package com.chaltteok.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway")
class GatewayProperties(
    val internalSecret: String = "",
    val publicPaths: List<String> = emptyList(),
    val ssePaths: List<String> = emptyList(),
    val ownerPaths: List<String> = emptyList(),
)
