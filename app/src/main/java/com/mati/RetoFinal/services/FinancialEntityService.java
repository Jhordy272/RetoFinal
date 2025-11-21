package com.mati.RetoFinal.services;

import com.mati.RetoFinal.dto.CreateFinancialEntityRequest;
import com.mati.RetoFinal.dto.FinancialEntityResponse;
import com.mati.RetoFinal.models.FinancialEntity;
import com.mati.RetoFinal.repositories.FinancialEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialEntityService {

    private final FinancialEntityRepository financialEntityRepository;

    @Transactional
    public FinancialEntityResponse createFinancialEntity(CreateFinancialEntityRequest request) {
        log.info("Creating financial entity with code: {}", request.getEntityCode());

        Optional<FinancialEntity> existing = financialEntityRepository.findByEntityCode(request.getEntityCode());
        if (existing.isPresent()) {
            log.error("Financial entity with code {} already exists", request.getEntityCode());
            throw new IllegalArgumentException("Financial entity with code " + request.getEntityCode() + " already exists");
        }

        FinancialEntity entity = FinancialEntity.builder()
                .entityCode(request.getEntityCode())
                .entityName(request.getEntityName())
                .webhookUrl(request.getWebhookUrl())
                .webhookAuthConfig(request.getWebhookAuthConfig())
                .timeoutMs(request.getTimeoutMs() != null ? request.getTimeoutMs() : 5000)
                .maxRetries(request.getMaxRetries() != null ? request.getMaxRetries() : 3)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        FinancialEntity saved = financialEntityRepository.save(entity);
        log.info("Financial entity created successfully with ID: {}", saved.getEntityId());

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<FinancialEntityResponse> findByEntityCode(String entityCode) {
        log.info("Finding financial entity by code: {}", entityCode);
        return financialEntityRepository.findByEntityCode(entityCode)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Optional<FinancialEntityResponse> findByEntityId(UUID entityId) {
        log.info("Finding financial entity by ID: {}", entityId);
        return financialEntityRepository.findById(entityId)
                .map(this::mapToResponse);
    }

    private FinancialEntityResponse mapToResponse(FinancialEntity entity) {
        return FinancialEntityResponse.builder()
                .entityId(entity.getEntityId())
                .entityCode(entity.getEntityCode())
                .entityName(entity.getEntityName())
                .webhookUrl(entity.getWebhookUrl())
                .timeoutMs(entity.getTimeoutMs())
                .maxRetries(entity.getMaxRetries())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
