package com.mati.RetoFinal.models;

/**
 * Enum representing the status of a key in the system.
 */
public enum KeyStatus {
    /**
     * Key is active and can be used for transactions
     */
    ACTIVE,

    /**
     * Key is temporarily suspended
     */
    SUSPENDED,

    /**
     * Key has been soft deleted
     */
    DELETED
}
