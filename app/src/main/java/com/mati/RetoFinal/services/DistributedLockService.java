package com.mati.RetoFinal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing distributed locks using Redisson.
 * Prevents race conditions in distributed systems.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {

    private static final String LOCK_PREFIX = "lock:key:";
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofSeconds(5);
    private static final Duration DEFAULT_LEASE_TIME = Duration.ofSeconds(10);

    private final RedissonClient redissonClient;

    /**
     * Acquire a lock with default wait and lease times
     */
    public boolean acquireLock(String key) {
        return acquireLock(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    /**
     * Acquire a lock with custom wait and lease times
     * @param key The key to lock
     * @param waitTime Maximum time to wait for lock
     * @param leaseTime Time after which lock is automatically released
     * @return true if lock was acquired, false otherwise
     */
    public boolean acquireLock(String key, Duration waitTime, Duration leaseTime) {
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(
                waitTime.toMillis(),
                leaseTime.toMillis(),
                TimeUnit.MILLISECONDS
            );

            if (acquired) {
                log.debug("Lock acquired for key: {}", key);
            } else {
                log.warn("Failed to acquire lock for key: {} after waiting {}ms", key, waitTime.toMillis());
            }

            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while acquiring lock for key: {}", key, e);
            return false;
        } catch (Exception e) {
            log.error("Error acquiring lock for key: {}. Error: {}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Release a lock
     * @param key The key to unlock
     */
    public void releaseLock(String key) {
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("Lock released for key: {}", key);
            } else {
                log.warn("Attempted to release lock for key: {} but it's not held by current thread", key);
            }
        } catch (Exception e) {
            log.error("Error releasing lock for key: {}. Error: {}", key, e.getMessage(), e);
        }
    }

    /**
     * Check if a lock is currently held
     */
    public boolean isLocked(String key) {
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * Force unlock (use with caution)
     */
    public void forceUnlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.forceUnlock();
            log.warn("Force unlock performed for key: {}", key);
        } catch (Exception e) {
            log.error("Error force unlocking key: {}. Error: {}", key, e.getMessage(), e);
        }
    }
}
