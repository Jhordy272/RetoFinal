package com.mati.RetoFinal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mati.RetoFinal.models.FinancialEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for FinancialEntity operations.
 */
@Repository
public interface FinancialEntityRepository extends JpaRepository<FinancialEntity, UUID> {

    /**
     * Find a financial entity by its code
     */
    Optional<FinancialEntity> findByEntityCode(String entityCode);

    /**
     * Find an active financial entity by its code
     */
    @Query("SELECT e FROM FinancialEntity e WHERE e.entityCode = :entityCode AND e.isActive = true")
    Optional<FinancialEntity> findByEntityCodeAndActive(@Param("entityCode") String entityCode);

    /**
     * Check if a financial entity exists by code
     */
    boolean existsByEntityCode(String entityCode);

    /**
     * Find all active financial entities
     */
    List<FinancialEntity> findByIsActiveTrue();

    /**
     * Find all inactive financial entities
     */
    List<FinancialEntity> findByIsActiveFalse();
}
