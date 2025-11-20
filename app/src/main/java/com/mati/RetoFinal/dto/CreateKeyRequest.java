package com.mati.RetoFinal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or checking a key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKeyRequest {

    @NotBlank(message = "Key value cannot be blank")
    @Size(max = 255, message = "Key value cannot exceed 255 characters")
    private String keyValue;

    @Size(max = 50, message = "Account number cannot exceed 50 characters")
    private String accountNumber;

    @Size(max = 50, message = "Owner document cannot exceed 50 characters")
    private String ownerDocument;

    @NotBlank(message = "Entity code cannot be blank")
    @Size(max = 20, message = "Entity code cannot exceed 20 characters")
    private String entityCode;
}
