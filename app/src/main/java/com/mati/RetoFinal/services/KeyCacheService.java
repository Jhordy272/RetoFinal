package com.mati.RetoFinal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mati.RetoFinal.dto.KeyCacheDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Service for caching key data in Redis.
 * Implements resilience pattern: never fail if Redis is down, only log errors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCacheService {

    private static final String KEY_PREFIX = "key:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Get a simple value from cache
     */
    public Optional<String> getValue(String key) {
        try {
            String cacheKey = KEY_PREFIX + key;
            String value = redisTemplate.opsForValue().get(cacheKey);
            if (value != null) {
                log.debug("Cache hit for key: {}", key);
                return Optional.of(value);
            }
            log.debug("Cache miss for key: {}", key);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting value from cache for key: {}. Error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Put a simple value into cache with TTL
     */
    public void putValue(String key, String value, Duration ttl) {
        try {
            String cacheKey = KEY_PREFIX + key;
            redisTemplate.opsForValue().set(cacheKey, value, ttl);
            log.debug("Cached value for key: {} with TTL: {}", key, ttl);
        } catch (Exception e) {
            log.error("Error putting value into cache for key: {}. Error: {}", key, e.getMessage());
        }
    }

    /**
     * Put a simple value into cache with default TTL
     */
    public void putValue(String key, String value) {
        putValue(key, value, DEFAULT_TTL);
    }

    /**
     * Put a KeyCacheDto into cache (serialized as JSON)
     */
    public void putKeyDto(String key, KeyCacheDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            putValue(key, json, DEFAULT_TTL);
            log.info("Cached KeyCacheDto for key: {}", key);
        } catch (JsonProcessingException e) {
            log.error("Error serializing KeyCacheDto for key: {}. Error: {}", key, e.getMessage());
        }
    }

    /**
     * Get a KeyCacheDto from cache (deserialized from JSON)
     */
    public Optional<KeyCacheDto> getKeyDto(String key) {
        try {
            Optional<String> jsonOpt = getValue(key);
            if (jsonOpt.isPresent()) {
                KeyCacheDto dto = objectMapper.readValue(jsonOpt.get(), KeyCacheDto.class);
                log.debug("Retrieved and deserialized KeyCacheDto for key: {}", key);
                return Optional.of(dto);
            }
            return Optional.empty();
        } catch (JsonProcessingException e) {
            log.error("Error deserializing KeyCacheDto for key: {}. Error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Evict a key from cache
     */
    public void evict(String key) {
        try {
            String cacheKey = KEY_PREFIX + key;
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("Evicted key from cache: {}", key);
            } else {
                log.debug("Key not found in cache for eviction: {}", key);
            }
        } catch (Exception e) {
            log.error("Error evicting key from cache: {}. Error: {}", key, e.getMessage());
        }
    }

    /**
     * Check if cache is available
     */
    public boolean isAvailable() {
        try {
            redisTemplate.opsForValue().get("health-check");
            return true;
        } catch (Exception e) {
            log.warn("Redis cache is not available: {}", e.getMessage());
            return false;
        }
    }
}
