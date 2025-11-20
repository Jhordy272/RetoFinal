package com.mati.RetoFinal.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a key in the system.
 * Stores key information with relationship to financial entities.
 */
@Entity
@Table(name = "keys", indexes = {
    @Index(name = "idx_key_value", columnList = "key_value", unique = true),
    @Index(name = "idx_account_number", columnList = "account_number"),
    @Index(name = "idx_owner_document", columnList = "owner_document"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_entity_status", columnList = "entity_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", updatable = false, nullable = false)
    private UUID keyId;

    @Column(name = "key_value", unique = true, nullable = false, length = 255)
    private String keyValue;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "owner_document", length = 50)
    private String ownerDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private KeyStatus status = KeyStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false, foreignKey = @ForeignKey(name = "fk_key_entity"))
    private FinancialEntity financialEntity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (keyId == null) {
            keyId = UUID.randomUUID();
        }
        if (status == null) {
            status = KeyStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the key is active
     */
    public boolean isActive() {
        return KeyStatus.ACTIVE.equals(status);
    }

    /**
     * Suspend this key
     */
    public void suspend() {
        this.status = KeyStatus.SUSPENDED;
    }

    /**
     * Activate this key
     */
    public void activate() {
        this.status = KeyStatus.ACTIVE;
    }

    /**
     * Soft delete this key
     */
    public void delete() {
        this.status = KeyStatus.DELETED;
    }
}
