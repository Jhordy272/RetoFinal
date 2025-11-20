package com.mati.RetoFinal.repositories;

import com.mati.RetoFinal.models.FinancialEntity;
import com.mati.RetoFinal.models.Key;
import com.mati.RetoFinal.models.KeyStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Key operations.
 */
@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {

    /**
     * Find a key by its value
     */
    Optional<Key> findByKeyValue(String keyValue);

    /**
     * Find a key by its value and status
     */
    Optional<Key> findByKeyValueAndStatus(String keyValue, KeyStatus status);

    /**
     * Check if a key exists by its value
     */
    boolean existsByKeyValue(String keyValue);

    /**
     * Check if an active key exists by its value
     */
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END " +
           "FROM Key k WHERE k.keyValue = :keyValue AND k.status = 'ACTIVE'")
    boolean existsByKeyValueAndActive(@Param("keyValue") String keyValue);

    /**
     * Find all keys by financial entity
     */
    List<Key> findByFinancialEntity(FinancialEntity financialEntity);

    /**
     * Find all keys by financial entity and status
     */
    List<Key> findByFinancialEntityAndStatus(FinancialEntity financialEntity, KeyStatus status);

    /**
     * Find all keys by account number
     */
    List<Key> findByAccountNumber(String accountNumber);

    /**
     * Find all keys by owner document
     */
    List<Key> findByOwnerDocument(String ownerDocument);

    /**
     * Find all keys by status
     */
    List<Key> findByStatus(KeyStatus status);

    /**
     * Suspend a key by its ID
     */
    @Modifying
    @Query("UPDATE Key k SET k.status = 'SUSPENDED', k.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE k.keyId = :keyId AND k.status = 'ACTIVE'")
    int suspendKey(@Param("keyId") UUID keyId);

    /**
     * Activate a key by its ID
     */
    @Modifying
    @Query("UPDATE Key k SET k.status = 'ACTIVE', k.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE k.keyId = :keyId AND k.status = 'SUSPENDED'")
    int activateKey(@Param("keyId") UUID keyId);

    /**
     * Soft delete a key by its ID
     */
    @Modifying
    @Query("UPDATE Key k SET k.status = 'DELETED', k.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE k.keyId = :keyId AND k.status != 'DELETED'")
    int softDeleteKey(@Param("keyId") UUID keyId);
}
