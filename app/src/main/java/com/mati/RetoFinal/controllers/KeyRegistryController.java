package com.mati.RetoFinal.controllers;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mati.RetoFinal.services.KeyCacheService;

import com.mati.RetoFinal.services.KeyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import com.mati.RetoFinal.dto.CreateKeyRequest;
import com.mati.RetoFinal.dto.CreateKeyResponse;
import com.mati.RetoFinal.dto.EntityKeyResponse;
import com.mati.RetoFinal.dto.UpdateKeyRequest;

/**
 * REST Controller for key registry operations.
 */
@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
@Slf4j
public class KeyRegistryController {

    private final KeyService keyService;
    private final KeyCacheService keyCacheService;

    /**
     * Check if a key exists or create it if it doesn't
     * POST /api/keys/check-or-create
     */
    @PostMapping("/check-or-create")
    public ResponseEntity<CreateKeyResponse> checkOrCreateKey(@Valid @RequestBody CreateKeyRequest request) {
        log.info("Received check-or-create request for key: {}", request.getKeyValue());

        CreateKeyResponse response = keyService.createKey(request);

        if (response.getMessage() != null && response.getMessage().contains("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get a key by its value
     * GET /api/keys/{keyValue}
     */
    @GetMapping("/{keyValue}")
    public ResponseEntity<EntityKeyResponse> getKeyByValue(@PathVariable String keyValue) {
        log.info("Received get request for key: {}", keyValue);

        Optional<EntityKeyResponse> response = keyService.findByValue(keyValue);

        return response
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a key by its ID
     * PUT /api/keys/{keyId}
     */
    @PutMapping("/{keyId}")
    public ResponseEntity<EntityKeyResponse> updateKey(
            @PathVariable UUID keyId,
            @Valid @RequestBody UpdateKeyRequest request) {
        log.info("Received update request for key ID: {}", keyId);

        Optional<EntityKeyResponse> response = keyService.updateKeyByKeyId(keyId, request);

        return response
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a key by its ID (soft delete)
     * DELETE /api/keys/{keyId}
     */
    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> deleteKey(@PathVariable UUID keyId) {
        log.info("Received delete request for key ID: {}", keyId);

        boolean deleted = keyService.deleteKeyById(keyId);

        return deleted
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    /**
     * Health check endpoint
     * GET /api/keys/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("service", "key-registry");
        healthStatus.put("redisAvailable", keyCacheService.isAvailable());

        return ResponseEntity.ok(healthStatus);
    }
}
