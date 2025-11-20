package com.mati.RetoFinal.services;

import com.mati.RetoFinal.dto.CreateKeyRequest;
import com.mati.RetoFinal.dto.CreateKeyResponse;
import com.mati.RetoFinal.dto.EntityKeyResponse;
import com.mati.RetoFinal.dto.KeyCacheDto;
import com.mati.RetoFinal.dto.UpdateKeyRequest;
import com.mati.RetoFinal.models.FinancialEntity;
import com.mati.RetoFinal.models.Key;
import com.mati.RetoFinal.models.KeyStatus;
import com.mati.RetoFinal.repositories.FinancialEntityRepository;
import com.mati.RetoFinal.repositories.KeyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Main service for key management operations.
 * Implements Cache-Aside pattern with distributed locks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyService {

    private final KeyRepository keyRepository;
    private final FinancialEntityRepository financialEntityRepository;
    private final KeyCacheService keyCacheService;
    private final DistributedLockService lockService;
    private final KeyValidator keyValidator;
    //private final KeyEventPublisher eventPublisher;

    /**
     * Create a key or return existing one.
     * Implements exact flow:
     * 1. Measure start time
     * 2. Validate format
     * 3. Check cache
     * 4. Acquire distributed lock
     * 5. Double-check in DB
     * 6. Create if not exists
     * 7. Cache and publish event
     * 8. Release lock
     * 9. Calculate latency and return
     */
    @Transactional
    public CreateKeyResponse createKey(CreateKeyRequest request) {
        long startTime = System.nanoTime();

        try {
            // Step 1: Validate format
            if (!keyValidator.validateKeyFormat(request.getKeyValue())) {
                double latencyMs = calculateLatencyMs(startTime);
                return CreateKeyResponse.error("Invalid key format", latencyMs);
            }

            // Step 2: Check cache first
            Optional<KeyCacheDto> cachedKey = keyCacheService.getKeyDto(request.getKeyValue());
            if (cachedKey.isPresent()) {
                double latencyMs = calculateLatencyMs(startTime);
                KeyCacheDto dto = cachedKey.get();
                log.info("Key found in cache: {} (latency: {}ms)", request.getKeyValue(), latencyMs);
                return CreateKeyResponse.exists(dto.getKeyId(), dto.getKeyValue(), latencyMs, "cache");
            }

            // Step 3: Acquire distributed lock
            boolean lockAcquired = lockService.acquireLock(request.getKeyValue());
            if (!lockAcquired) {
                double latencyMs = calculateLatencyMs(startTime);
                log.warn("Failed to acquire lock for key: {}", request.getKeyValue());
                return CreateKeyResponse.error("Failed to acquire lock, please retry", latencyMs);
            }

            try {
                // Step 4: Double-check in database (after acquiring lock)
                Optional<Key> existingKey = keyRepository.findByKeyValueAndStatus(
                    request.getKeyValue(),
                    KeyStatus.ACTIVE
                );

                if (existingKey.isPresent()) {
                    Key key = existingKey.get();
                    // Cache it for next time
                    keyCacheService.putKeyDto(request.getKeyValue(), KeyCacheDto.fromEntity(key));
                    double latencyMs = calculateLatencyMs(startTime);
                    log.info("Key found in database: {} (latency: {}ms)", request.getKeyValue(), latencyMs);
                    return CreateKeyResponse.exists(key.getKeyId(), key.getKeyValue(), latencyMs, "database");
                }

                // Step 5: Key doesn't exist, create new one
                // Get financial entity
                FinancialEntity entity = financialEntityRepository
                    .findByEntityCodeAndActive(request.getEntityCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Financial entity not found or inactive: " + request.getEntityCode()
                    ));

                // Create new key
                Key newKey = Key.builder()
                    .keyValue(request.getKeyValue())
                    .accountNumber(request.getAccountNumber())
                    .ownerDocument(request.getOwnerDocument())
                    .status(KeyStatus.ACTIVE)
                    .financialEntity(entity)
                    .build();

                // Save to database
                Key savedKey = keyRepository.save(newKey);

                // Cache the new key
                keyCacheService.putKeyDto(savedKey.getKeyValue(), KeyCacheDto.fromEntity(savedKey));

                // Publish event
                //eventPublisher.publishKeyRegistered(savedKey);

                double latencyMs = calculateLatencyMs(startTime);
                log.info("New key created: {} (latency: {}ms)", savedKey.getKeyValue(), latencyMs);

                return CreateKeyResponse.created(savedKey.getKeyId(), savedKey.getKeyValue(), latencyMs);

            } finally {
                // Step 6: Always release lock
                lockService.releaseLock(request.getKeyValue());
            }

        } catch (IllegalArgumentException e) {
            double latencyMs = calculateLatencyMs(startTime);
            log.error("Validation error creating key: {}", e.getMessage());
            return CreateKeyResponse.error(e.getMessage(), latencyMs);
        } catch (Exception e) {
            double latencyMs = calculateLatencyMs(startTime);
            log.error("Error creating key: {}", e.getMessage(), e);
            return CreateKeyResponse.error("Internal error: " + e.getMessage(), latencyMs);
        }
    }

    /**
     * Find a key by its value (cache first, then database)
     */
    public Optional<EntityKeyResponse> findByValue(String keyValue) {
        // Try cache first
        Optional<KeyCacheDto> cachedKey = keyCacheService.getKeyDto(keyValue);
        if (cachedKey.isPresent()) {
            log.info("Key found in cache: {}", keyValue);
            return Optional.of(EntityKeyResponse.fromCache(cachedKey.get()));
        }

        // Not in cache, check database
        Optional<Key> dbKey = keyRepository.findByKeyValue(keyValue);
        if (dbKey.isPresent()) {
            Key key = dbKey.get();
            // Cache it for next time
            keyCacheService.putKeyDto(keyValue, KeyCacheDto.fromEntity(key));
            log.info("Key found in database and cached: {}", keyValue);
            return Optional.of(EntityKeyResponse.fromEntity(key, "database"));
        }

        log.info("Key not found: {}", keyValue);
        return Optional.empty();
    }

    /**
     * Update a key by its ID
     */
    @Transactional
    public Optional<EntityKeyResponse> updateKeyByKeyId(UUID keyId, UpdateKeyRequest request) {
        Optional<Key> keyOpt = keyRepository.findById(keyId);
        if (keyOpt.isEmpty()) {
            return Optional.empty();
        }

        Key key = keyOpt.get();

        // Update fields if provided
        if (request.getAccountNumber() != null) {
            key.setAccountNumber(request.getAccountNumber());
        }
        if (request.getOwnerDocument() != null) {
            key.setOwnerDocument(request.getOwnerDocument());
        }
        if (request.getStatus() != null) {
            try {
                KeyStatus newStatus = KeyStatus.valueOf(request.getStatus().toUpperCase());
                key.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status provided: {}", request.getStatus());
            }
        }

        // Save changes
        Key updatedKey = keyRepository.save(key);

        // Invalidate cache
        keyCacheService.evict(updatedKey.getKeyValue());

        // Publish event
        //eventPublisher.publishKeyUpdated(updatedKey);

        log.info("Key updated: {}", keyId);
        return Optional.of(EntityKeyResponse.fromEntity(updatedKey, "database"));
    }

    /**
     * Soft delete a key by its ID
     */
    @Transactional
    public boolean deleteKeyById(UUID keyId) {
        Optional<Key> keyOpt = keyRepository.findById(keyId);
        if (keyOpt.isEmpty()) {
            return false;
        }

        Key key = keyOpt.get();
        key.delete();
        keyRepository.save(key);

        // Invalidate cache
        keyCacheService.evict(key.getKeyValue());

        // Publish event
        //eventPublisher.publishKeyDeleted(key);

        log.info("Key soft deleted: {}", keyId);
        return true;
    }

    /**
     * Calculate latency in milliseconds from start time
     */
    private double calculateLatencyMs(long startNanos) {
        long endNanos = System.nanoTime();
        return (endNanos - startNanos) / 1_000_000.0;
    }
}
