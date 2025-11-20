package com.mati.RetoFinal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mati.RetoFinal.repositories.KeyRepository;

/**
 * Service for validating key business rules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyValidator {

    private static final int MAX_KEY_LENGTH = 255;

    private final KeyRepository keyRepository;

    /**
     * Validate that a key does not already exist in the database
     */
    public boolean validateKeyDoesNotExist(String keyValue) {
        boolean exists = keyRepository.existsByKeyValue(keyValue);
        if (exists) {
            log.debug("Validation failed: key already exists - {}", keyValue);
            return false;
        }
        return true;
    }

    /**
     * Validate key format
     * - Must not be null or empty
     * - Must not exceed maximum length
     */
    public boolean validateKeyFormat(String keyValue) {
        if (keyValue == null || keyValue.trim().isEmpty()) {
            log.warn("Validation failed: key is null or empty");
            return false;
        }

        if (keyValue.length() > MAX_KEY_LENGTH) {
            log.warn("Validation failed: key length {} exceeds maximum length {}", keyValue.length(), MAX_KEY_LENGTH);
            return false;
        }

        log.debug("Key format validation passed for key: {}", keyValue);
        return true;
    }

    /**
     * Comprehensive validation: format + uniqueness
     */
    public boolean validateKey(String keyValue) {
        return validateKeyFormat(keyValue) && validateKeyDoesNotExist(keyValue);
    }
}
