package com.mati.RetoFinal.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a financial institution in the system.
 */
@Entity
@Table(name = "financial_entities", indexes = {
    @Index(name = "idx_entity_code", columnList = "entity_code", unique = true),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "entity_id", updatable = false, nullable = false)
    private UUID entityId;

    @Column(name = "entity_code", unique = true, nullable = false, length = 20)
    private String entityCode;

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "webhook_auth_config", columnDefinition = "TEXT")
    private String webhookAuthConfig;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (entityId == null) {
            entityId = UUID.randomUUID();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
