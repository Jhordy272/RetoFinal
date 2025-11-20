package com.mati.RetoFinal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mati.RetoFinal.models.Key;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for publishing key-related events via Redis Pub/Sub.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyEventPublisher {

    private static final String CHANNEL_PREFIX = "key-events:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publish a key registered event
     */
    public void publishKeyRegistered(Key key) {
        publishEvent("KEY_REGISTERED", key);
    }

    /**
     * Publish a key updated event
     */
    public void publishKeyUpdated(Key key) {
        publishEvent("KEY_UPDATED", key);
    }

    /**
     * Publish a key suspended event
     */
    public void publishKeySuspended(Key key) {
        publishEvent("KEY_SUSPENDED", key);
    }

    /**
     * Publish a key deleted event
     */
    public void publishKeyDeleted(Key key) {
        publishEvent("KEY_DELETED", key);
    }

    /**
     * Internal method to publish events
     */
    private void publishEvent(String eventType, Key key) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("keyId", key.getKeyId().toString());
            event.put("keyValue", key.getKeyValue());
            event.put("status", key.getStatus().toString());
            event.put("accountNumber", key.getAccountNumber());
            event.put("ownerDocument", key.getOwnerDocument());
            event.put("entityCode", key.getFinancialEntity() != null ? key.getFinancialEntity().getEntityCode() : null);
            event.put("timestamp", LocalDateTime.now().toString());

            String eventJson = objectMapper.writeValueAsString(event);
            String channel = CHANNEL_PREFIX + eventType.toLowerCase();

            redisTemplate.convertAndSend(channel, eventJson);
            log.info("Published event {} for key: {}", eventType, key.getKeyValue());
        } catch (JsonProcessingException e) {
            log.error("Error serializing event {} for key: {}. Error: {}", eventType, key.getKeyValue(), e.getMessage());
        } catch (Exception e) {
            log.error("Error publishing event {} for key: {}. Error: {}", eventType, key.getKeyValue(), e.getMessage());
        }
    }
}
