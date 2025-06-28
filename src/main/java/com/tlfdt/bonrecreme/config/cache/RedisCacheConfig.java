package com.tlfdt.bonrecreme.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tlfdt.bonrecreme.config.cache.mixin.PageImplMixin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * Configures the Redis cache, emphasizing security and best practices.
 * This configuration includes a custom deserializer to handle Spring Data Page objects.
 */
@Configuration
public class RedisCacheConfig {

    @Value("${cache.default.ttl:3600}")
    private long defaultTtlSeconds;

    /**
     * Creates a secure Jackson ObjectMapper for Redis serialization.
     * This ObjectMapper is enhanced with modules for Java 8 Time and for handling
     * the deserialization of Spring Data's Page interface.
     *
     * @return A securely configured ObjectMapper bean.
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.addMixIn(PageImpl.class, PageImplMixin.class);

        // The problematic default typing has been removed to avoid conflicts
        // with the default ObjectMapper used for HTTP message conversion.

        return objectMapper;
    }

    /**
     * Defines the central cache configuration for the application.
     *
     * @param objectMapper The securely configured ObjectMapper for serialization.
     * @return The RedisCacheConfiguration bean.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultTtlSeconds))
                .disableCachingNullValues()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }

    @Bean
    public CommandLineRunner clearCacheOnStartup(CacheManager cacheManager) {
        return args -> {
            for (String name : cacheManager.getCacheNames()) {
                Objects.requireNonNull(cacheManager.getCache(name)).clear();
            }
        };
    }
}