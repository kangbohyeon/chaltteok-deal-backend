package com.chaltteok.owner.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory, objectMapper: ObjectMapper): CacheManager {
        val cacheMapper = objectMapper.copy().activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Any::class.java)
                .build(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY,
        )
        val serializer = GenericJackson2JsonRedisSerializer(cacheMapper)
        val valuePair = RedisSerializationContext.SerializationPair.fromSerializer(serializer)

        val base = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(valuePair)
            .disableCachingNullValues()

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(base.entryTtl(Duration.ofSeconds(120)))
            .withCacheConfiguration("product", base.entryTtl(Duration.ofSeconds(300)))
            .withCacheConfiguration("productList", base.entryTtl(Duration.ofSeconds(120)))
            .withCacheConfiguration("productRecommended", base.entryTtl(Duration.ofSeconds(120)))
            .build()
    }
}
