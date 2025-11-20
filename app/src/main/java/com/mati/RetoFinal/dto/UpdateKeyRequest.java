package com.mati.RetoFinal.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateKeyRequest {

    @Size(max = 50, message = "Account number cannot exceed 50 characters")
    private String accountNumber;

    @Size(max = 50, message = "Owner document cannot exceed 50 characters")
    private String ownerDocument;

    private String status; // ACTIVE, SUSPENDED, DELETED
}
