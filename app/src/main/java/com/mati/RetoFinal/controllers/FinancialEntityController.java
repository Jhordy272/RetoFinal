package com.mati.RetoFinal.controllers;

import com.mati.RetoFinal.dto.CreateFinancialEntityRequest;
import com.mati.RetoFinal.dto.FinancialEntityResponse;
import com.mati.RetoFinal.services.FinancialEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-entities")
@RequiredArgsConstructor
@Slf4j
public class FinancialEntityController {

    private final FinancialEntityService financialEntityService;

    /**
     * Create a new financial entity
     * POST /api/financial-entities
     */
    @PostMapping
    public ResponseEntity<FinancialEntityResponse> createFinancialEntity(
            @Valid @RequestBody CreateFinancialEntityRequest request) {
        log.info("Received request to create financial entity: {}", request.getEntityCode());

        try {
            FinancialEntityResponse response = financialEntityService.createFinancialEntity(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error creating financial entity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Get financial entity by code
     * GET /api/financial-entities/code/{entityCode}
     */
    @GetMapping("/code/{entityCode}")
    public ResponseEntity<FinancialEntityResponse> getByEntityCode(@PathVariable String entityCode) {
        log.info("Received request to get financial entity by code: {}", entityCode);

        Optional<FinancialEntityResponse> response = financialEntityService.findByEntityCode(entityCode);

        return response
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get financial entity by ID
     * GET /api/financial-entities/{entityId}
     */
    @GetMapping("/{entityId}")
    public ResponseEntity<FinancialEntityResponse> getByEntityId(@PathVariable UUID entityId) {
        log.info("Received request to get financial entity by ID: {}", entityId);

        Optional<FinancialEntityResponse> response = financialEntityService.findByEntityId(entityId);

        return response
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
